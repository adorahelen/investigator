package hycu.investigator.msa.controller;

import hycu.investigator.msa.service.PrivacyDetectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletableFuture; // CompletableFuture 임포트

@RestController
@RequestMapping("/api/detect")
public class PrivacyDetectionController {

    private final PrivacyDetectionService privacyDetectionService;

    public PrivacyDetectionController(PrivacyDetectionService privacyDetectionService) {
        this.privacyDetectionService = privacyDetectionService;
    }

    // 파일 업로드 및 비동기 작업 시작 엔드포인트
    @PostMapping("/file")
    public ResponseEntity<?> detectPrivacyInFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "업로드된 파일이 없습니다."));
        }

        try {
            String taskId = UUID.randomUUID().toString(); // 고유한 작업 ID 생성
            // 파일 바이트 배열로 변환 (InputStream은 한번만 읽을 수 있으므로, 비동기 메서드에서 재사용하기 위함)
            byte[] fileBytes = file.getBytes();
            privacyDetectionService.startPrivacyDetection(taskId, fileBytes, file.getOriginalFilename());

            return ResponseEntity.ok(Collections.singletonMap("taskId", taskId)); // 작업 ID만 바로 응답
        } catch (IOException e) {
            System.err.println("파일 바이트 읽기 중 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "파일 데이터를 읽는 중 문제가 발생했습니다: " + e.getMessage()));
        }
    }

    // 작업 상태 및 결과 폴링 엔드포인트
    @GetMapping("/status/{taskId}")
    public ResponseEntity<PrivacyDetectionService.AsyncTaskResult> getTaskStatus(@PathVariable String taskId) {
        PrivacyDetectionService.AsyncTaskResult result = privacyDetectionService.getTaskResult(taskId);
        // 결과가 완료되면 taskResults 맵에서 제거하여 메모리 관리 (선택 사항)
        if (result != null && (result.getStatus() == PrivacyDetectionService.AsyncTaskResult.Status.COMPLETED ||
                result.getStatus() == PrivacyDetectionService.AsyncTaskResult.Status.FAILED)) {
            // 필요에 따라 작업 완료 후 taskResults.remove(taskId); 를 호출하여 메모리 해제
            // 여기서는 예시를 위해 유지
        }
        return ResponseEntity.ok(result);
    }
}