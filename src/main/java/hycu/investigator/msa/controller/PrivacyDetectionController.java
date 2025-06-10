package hycu.investigator.msa.controller;


import hycu.investigator.msa.service.PrivacyDetectionService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController // RESTful 웹 서비스 컨트롤러임을 명시
@RequestMapping("/api/detect") // 기본 URL 경로 설정
public class PrivacyDetectionController {

    private final PrivacyDetectionService privacyDetectionService;

    // 스프링이 PrivacyDetectionService 빈을 자동으로 주입
    public PrivacyDetectionController(PrivacyDetectionService privacyDetectionService) {
        this.privacyDetectionService = privacyDetectionService;
    }

    @PostMapping("/file")
    public ResponseEntity<?> detectPrivacyInFile(@RequestParam("file") MultipartFile file) {
        if (file.isEmpty()) {
            return ResponseEntity.badRequest().body(Collections.singletonMap("error", "업로드된 파일이 없습니다."));
        }

        try {
            // MultipartFile에서 InputStream과 원본 파일명 추출
            Map<String, List<String>> results = privacyDetectionService.detectPrivacy(file.getInputStream(), file.getOriginalFilename());
            return ResponseEntity.ok(results); // 성공 시 200 OK와 결과 반환
        } catch (IOException e) {
            System.err.println("파일 처리 중 I/O 오류 발생: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "파일 처리 중 문제가 발생했습니다: " + e.getMessage()));
        } catch (Exception e) { // 그 외 예상치 못한 모든 예외 처리
            System.err.println("개인정보 탐지 중 예기치 않은 오류 발생: " + e.getMessage());
            e.printStackTrace(); // 개발 중에는 스택 트레이스 출력
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("error", "개인정보 탐지 중 예기치 않은 오류가 발생했습니다."));
        }
    }
}