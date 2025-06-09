package hycu.investigator.msa.service;

import hycu.investigator.msa.pattern.PrivacyPatternProvider;
import hycu.investigator.msa.detector.PrivacyDetector;
import hycu.investigator.msa.domain.PrivacyPattern;
import hycu.investigator.msa.extractor.ContentExtractor;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.*;

public class PrivacyDetectionService {

    private final ExecutorService executorService;
    private final List<PrivacyPattern> patterns;
    private final List<ContentExtractor> contentExtractors; // 여러 추출기 지원

    public PrivacyDetectionService(ExecutorService executorService, PrivacyPatternProvider patternProvider, List<ContentExtractor> contentExtractors) {
        this.executorService = executorService;
        this.patterns = patternProvider.getAllPatterns();
        this.contentExtractors = contentExtractors;
    }

    public Map<String, List<String>> detectPrivacy(Path filePath) throws IOException {
        String fileContent = "";
        boolean extracted = false;

        // 적절한 ContentExtractor 찾아서 사용
        for (ContentExtractor extractor : contentExtractors) {
            if (extractor.supports(filePath)) {
                fileContent = extractor.extract(filePath);
                extracted = true;
                break;
            }
        }

        if (!extracted) {
            throw new IOException("지원하지 않는 파일 형식 또는 추출기 없음: " + filePath);
        }

        if (fileContent.trim().isEmpty()) {
            System.out.println("파일에서 추출된 텍스트가 비어 있습니다. 개인정보를 탐지할 수 없습니다.");
            return new HashMap<>(); // 빈 결과 반환
        }

        // 모든 패턴 탐지 작업을 ExecutorService에 제출
        List<Future<List<String>>> futures = new ArrayList<>();
        for (PrivacyPattern p : patterns) {
            Callable<List<String>> detectorTask = new PrivacyDetector(fileContent, p.getName(), p.getRegex());
            futures.add(executorService.submit(detectorTask));
        }

        // 모든 작업의 결과 취합
        Map<String, List<String>> detectionResults = new HashMap<>();
        for (int i = 0; i < futures.size(); i++) {
            PrivacyPattern p = patterns.get(i);
            Future<List<String>> future = futures.get(i);
            try {
                List<String> foundItems = future.get();
                if (!foundItems.isEmpty()) {
                    detectionResults.put(p.getName(), foundItems);
                }
            } catch (InterruptedException | ExecutionException e) {
                System.err.println("🚨 오류 발생 (" + p.getName() + "): " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("  상세: " + e.getCause().getMessage());
                }
            }
        }
        return detectionResults;
    }
}