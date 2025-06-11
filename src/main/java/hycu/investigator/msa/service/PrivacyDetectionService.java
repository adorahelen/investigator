package hycu.investigator.msa.service;

import hycu.investigator.msa.detector.PrivacyDetector;
import hycu.investigator.msa.domain.PrivacyPattern;
import hycu.investigator.msa.extractor.ContentExtractor;
import hycu.investigator.msa.pattern.PrivacyPatternProvider;
import org.springframework.scheduling.annotation.Async; // @Async 임포트
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.*;

@Service
public class PrivacyDetectionService {

    private final ExecutorService executorService;
    private final List<PrivacyPattern> patterns;
    private final List<ContentExtractor> contentExtractors;
    // 비동기 작업 결과를 저장할 임시 저장소 (실제 운영 환경에서는 Redis/DB 등 사용)
    private final Map<String, AsyncTaskResult> taskResults = new ConcurrentHashMap<>();

    public PrivacyDetectionService(ExecutorService executorService, PrivacyPatternProvider patternProvider, List<ContentExtractor> contentExtractors) {
        this.executorService = executorService;
        this.patterns = patternProvider.getAllPatterns();
        this.contentExtractors = contentExtractors;
    }

    // 비동기 작업 상태를 나타낼 클래스
    public static class AsyncTaskResult {
        public enum Status { PENDING, PROCESSING, COMPLETED, FAILED }
        private Status status;
        private Map<String, List<String>> result;
        private String errorMessage;

        public AsyncTaskResult(Status status) {
            this.status = status;
        }

        public Status getStatus() { return status; }
        public void setStatus(Status status) { this.status = status; }
        public Map<String, List<String>> getResult() { return result; }
        public void setResult(Map<String, List<String>> result) { this.result = result; }
        public String getErrorMessage() { return errorMessage; }
        public void setErrorMessage(String errorMessage) { this.errorMessage = errorMessage; }
    }

    // 비동기 작업을 시작하고, 작업 ID를 반환
    @Async // 이 메서드는 별도의 스레드에서 실행됩니다.
    public CompletableFuture<Void> startPrivacyDetection(String taskId, byte[] fileBytes, String originalFilename) {
        taskResults.put(taskId, new AsyncTaskResult(AsyncTaskResult.Status.PROCESSING)); // 작업 시작 상태로 변경

        return CompletableFuture.runAsync(() -> {
            try (InputStream fileInputStream = new java.io.ByteArrayInputStream(fileBytes)) { // 바이트 배열로 InputStream 재생성
                Map<String, List<String>> detectionResult = performDetection(fileInputStream, originalFilename);
                AsyncTaskResult result = taskResults.get(taskId);
                result.setStatus(AsyncTaskResult.Status.COMPLETED);
                result.setResult(detectionResult);
            } catch (IOException e) {
                System.err.println("파일 처리 중 I/O 오류 발생 (비동기): " + e.getMessage());
                AsyncTaskResult result = taskResults.get(taskId);
                result.setStatus(AsyncTaskResult.Status.FAILED);
                result.setErrorMessage("파일 처리 중 문제가 발생했습니다: " + e.getMessage());
            } catch (Exception e) {
                System.err.println("개인정보 탐지 중 예기치 않은 오류 발생 (비동기): " + e.getMessage());
                e.printStackTrace();
                AsyncTaskResult result = taskResults.get(taskId);
                result.setStatus(AsyncTaskResult.Status.FAILED);
                result.setErrorMessage("개인정보 탐지 중 예기치 않은 오류가 발생했습니다: " + e.getMessage());
            }
        }, executorService); // @Async의 기본 Executor를 사용하거나, 특정 Executor를 지정할 수 있습니다.
    }

    // 실제 탐지 로직을 수행하는 내부 메서드
    private Map<String, List<String>> performDetection(InputStream fileInputStream, String originalFilename) throws IOException {
        String fileContent = null;
        boolean extracted = false;

        // InputStream은 한 번만 읽을 수 있으므로, 각 추출기 시도 전에 재생성 또는 복사 필요
        // 여기서는 바이트 배열로 복사된 InputStream을 사용하므로 문제 없습니다.
        for (ContentExtractor extractor : contentExtractors) {
            if (extractor.supports(originalFilename)) {
                // IMPORTANT: InputStream은 한번 읽으면 소진되므로, 여기서 각 extractor에게
                // 새로운 InputStream 인스턴스를 제공해야 합니다.
                // ByteArrayInputStream을 사용하면 여러 번 재생성 가능합니다.
                fileContent = extractor.extract(fileInputStream, originalFilename); // 이미 ByteArrayInputStream이므로 괜찮음
                extracted = true;
                break;
            }
        }

        if (!extracted || fileContent == null) {
            throw new IOException("지원하지 않는 파일 형식 또는 텍스트 추출 실패: " + originalFilename);
        }

        if (fileContent.trim().isEmpty()) {
            System.out.println("파일에서 추출된 텍스트가 비어 있습니다. 개인정보를 탐지할 수 없습니다.");
            return Collections.emptyMap(); // 빈 Map 반환
        }

        List<Future<List<String>>> futures = new ArrayList<>();
        for (PrivacyPattern p : patterns) {
            Callable<List<String>> detectorTask = new PrivacyDetector(fileContent, p.getName(), p.getRegex());
            futures.add(executorService.submit(detectorTask));
        }

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

    public AsyncTaskResult getTaskResult(String taskId) {
        return taskResults.getOrDefault(taskId, new AsyncTaskResult(AsyncTaskResult.Status.PENDING));
    }
}