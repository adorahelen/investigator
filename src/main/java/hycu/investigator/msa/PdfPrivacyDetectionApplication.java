package hycu.investigator.msa;

import hycu.investigator.msa.extractor.ContentExtractor;
import hycu.investigator.msa.extractor.PdfContentExtractor;
import hycu.investigator.msa.extractor.PlainTextFileContentExtractor;
import hycu.investigator.msa.pattern.PrivacyPatternProvider;
import hycu.investigator.msa.service.PrivacyDetectionService;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class PdfPrivacyDetectionApplication {

    private static final int THREAD_POOL_SIZE = 4; // 스레드 풀 크기

    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);

        // 의존성 주입 (수동 DI)
        PrivacyPatternProvider patternProvider = new PrivacyPatternProvider();
        List<ContentExtractor> contentExtractors = Arrays.asList(
                new PdfContentExtractor(),
                new PlainTextFileContentExtractor()
        );
        PrivacyDetectionService detectionService = new PrivacyDetectionService(executorService, patternProvider, contentExtractors);

        Scanner scanner = new Scanner(System.in);

        System.out.println("--- PDF/Text 파일 개인정보 탐지 솔루션 시작 ---");
        System.out.println("탐지할 파일 경로를 입력하세요. (종료: 'exit' 또는 'quit')");

        while (true) {
            System.out.print("\n파일 경로 입력 > ");
            String filePathString = scanner.nextLine();

            if (filePathString.equalsIgnoreCase("exit") || filePathString.equalsIgnoreCase("quit")) {
                System.out.println("프로그램을 종료합니다...");
                break;
            }

            Path filePath = Paths.get(filePathString);

            try {
                // 파일 존재 여부 및 읽기 권한 확인
                if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
                    System.err.println("오류: 파일을 찾을 수 없거나 읽을 수 없습니다 - " + filePathString);
                    System.out.println("정확한 파일 경로를 다시 입력해주세요.");
                    continue;
                }

                // 개인정보 탐지 서비스 호출
                Map<String, List<String>> results = detectionService.detectPrivacy(filePath);

                // 결과 출력
                System.out.println("\n--- 탐지 결과 ---");
                boolean foundAny = false;
                for (Map.Entry<String, List<String>> entry : results.entrySet()) {
                    if (!entry.getValue().isEmpty()) {
                        System.out.println("✅ " + entry.getKey() + " 탐지됨: " + entry.getValue());
                        foundAny = true;
                    }
                }
                if (!foundAny) {
                    System.out.println("어떤 개인정보도 탐지되지 않았습니다.");
                }

            } catch (IOException e) {
                System.err.println("오류: 파일을 처리하는 중 예기치 않은 문제 발생 - " + e.getMessage());
                if (e.getCause() != null) {
                    System.err.println("  원인: " + e.getCause().getMessage());
                }
                System.out.println("정확한 파일 경로를 다시 입력해주세요.");
            }
        }

        // 프로그램 종료 시 ExecutorService 종료
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
                System.out.println("스레드 풀이 강제 종료되었습니다. (일부 작업 미완료)");
            } else {
                System.out.println("스레드 풀이 정상적으로 종료되었습니다.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
            System.err.println("스레드 풀 종료 중 인터럽트 발생!");
        }

        scanner.close();
        System.out.println("솔루션이 종료되었습니다.");
    }
}
