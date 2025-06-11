//package hycu.investigator.msa.service;
//
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//import java.util.concurrent.*;
//
//import hycu.investigator.msa.detector.PrivacyDetector;
//import hycu.investigator.msa.domain.PrivacyPattern;
//import hycu.investigator.msa.extractor.ContentExtractor;
//import hycu.investigator.msa.pattern.PrivacyPatternProvider;
//import org.springframework.stereotype.Service; // 스프링의 @Service 어노테이션 추가
//
//@Service // 스프링 컴포넌트 스캔 대상이 되도록
//public class PrivacyDetectionServiceSync {
//
//    private final ExecutorService executorService;
//    private final List<PrivacyPattern> patterns;
//    private final List<ContentExtractor> contentExtractors;
//
//    // 스프링이 의존성을 주입하도록 생성자 기반 DI 사용
//    public PrivacyDetectionServiceSync(ExecutorService executorService, PrivacyPatternProvider patternProvider, List<ContentExtractor> contentExtractors) {
//        this.executorService = executorService;
//        this.patterns = patternProvider.getAllPatterns();
//        this.contentExtractors = contentExtractors;
//    }
//
//    public Map<String, List<String>> detectPrivacy(InputStream fileInputStream, String originalFilename) throws IOException {
//        String fileContent = null; // null로 초기화
//        boolean extracted = false;
//
//        // 적절한 ContentExtractor 찾아서 사용
//        for (ContentExtractor extractor : contentExtractors) {
//            if (extractor.supports(originalFilename)) {
//                // InputStream은 한 번만 읽을 수 있으므로, 각 추출기 시도 전에 재생성 또는 복사 필요
//                // 여기서는 간단하게 첫 번째 지원하는 추출기로 시도하고 실패하면 에러를 던지도록 합니다.
//                // 실제 프로덕션에서는 ByteArrayInputStream으로 복사하여 여러 추출기가 시도하도록 하는 것이 좋습니다.
//                fileContent = extractor.extract(fileInputStream, originalFilename);
//                extracted = true;
//                break;
//            }
//        }
//
//        if (!extracted || fileContent == null) {
//            throw new IOException("지원하지 않는 파일 형식 또는 텍스트 추출 실패: " + originalFilename);
//        }
//
//        if (fileContent.trim().isEmpty()) {
//            System.out.println("파일에서 추출된 텍스트가 비어 있습니다. 개인정보를 탐지할 수 없습니다.");
//            return new HashMap<>(); // 빈 결과 반환
//        }
//
//        // 모든 패턴 탐지 작업을 ExecutorService에 제출
//        List<Future<List<String>>> futures = new ArrayList<>();
//        for (PrivacyPattern p : patterns) {
//            Callable<List<String>> detectorTask = new PrivacyDetector(fileContent, p.getName(), p.getRegex());
//            futures.add(executorService.submit(detectorTask));
//        }
//
//        // 모든 작업의 결과 취합
//        Map<String, List<String>> detectionResults = new HashMap<>();
//        for (int i = 0; i < futures.size(); i++) {
//            PrivacyPattern p = patterns.get(i);
//            Future<List<String>> future = futures.get(i);
//            try {
//                List<String> foundItems = future.get();
//                if (!foundItems.isEmpty()) {
//                    detectionResults.put(p.getName(), foundItems);
//                }
//            } catch (InterruptedException | ExecutionException e) {
//                System.err.println("🚨 오류 발생 (" + p.getName() + "): " + e.getMessage());
//                if (e.getCause() != null) {
//                    System.err.println("  상세: " + e.getCause().getMessage());
//                }
//            }
//        }
//        return detectionResults;
//    }
//}