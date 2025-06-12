//package hycu.investigator.test;
//
//import java.io.IOException;
//import java.io.InputStream;
//import java.nio.file.Files;
//import java.nio.file.Path;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//import java.util.concurrent.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//
//// Apache Tika 관련 import
//import org.apache.tika.exception.TikaException;
//import org.apache.tika.metadata.Metadata;
//import org.apache.tika.parser.AutoDetectParser;
//import org.apache.tika.parser.ParseContext;
//import org.apache.tika.sax.BodyContentHandler; // Tika 2.x에서 더 견고한 처리를 위해 ContentHandlerFactory 고려
//
//import org.xml.sax.SAXException; // Tika 파싱 중 발생할 수 있는 예외
//
//// --- 개인정보 탐지 작업을 정의하는 Callable 클래스 (기존과 동일) ---
//class PrivacyDetector implements Callable<List<String>> {
//    private String text;
//    private String patternName;
//    private Pattern pattern;
//
//    public PrivacyDetector(String text, String patternName, String regex) {
//        this.text = text;
//        this.patternName = patternName;
//        this.pattern = Pattern.compile(regex); // 정규식 컴파일
//    }
//
//    @Override
//    public List<String> call() throws Exception {
//        List<String> foundItems = new ArrayList<>();
//        Matcher matcher = pattern.matcher(text);
//
//        // System.out.println(Thread.currentThread().getName() + " - Detecting " + patternName + "..."); // 너무 많은 출력으로 주석 처리
//
//        while (matcher.find()) {
//            foundItems.add(matcher.group());
//        }
//
//        // 탐지 작업 시간 시뮬레이션 (네트워크 지연, 복잡한 연산 등)
//        Thread.sleep((long) (Math.random() * 50) + 10); // 시간을 줄여 빠르게 테스트
//
//        return foundItems;
//    }
//}
//
//// --- 메인 솔루션 클래스 ---
//public class PdfPrivacyDetectionExample {
//
//    private static final int THREAD_POOL_SIZE = 4; // 스레드 풀 크기
//    private static ExecutorService executorService; // 스레드 풀 객체
//
//    // 개인정보 패턴 정의 (예시)
//    private static final List<PrivacyPattern> patterns = new ArrayList<>();
//
//    static {
//        // 한국 전화번호 정규식 예시: 010-1234-5678, 02-123-4567 등
//        patterns.add(new PrivacyPattern("Phone Number", "\\b01[016789][-.\\s]?\\d{3,4}[-.\\s]?\\d{4}\\b|\\b02[-.\\s]?\\d{3,4}[-.\\s]?\\d{4}\\b"));
//        patterns.add(new PrivacyPattern("Email Address", "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\\b"));
//        // 주민등록번호 (가상 패턴, 실제 환경에서 보안 및 법적 문제로 사용 주의)
//        patterns.add(new PrivacyPattern("Korean SSN (Dummy)", "\\b\\d{6}-[1-4]\\d{6}\\b"));
//        // 신용카드 번호 (가상 패턴, 실제 환경에서 보안 및 법적 문제로 사용 주의)
//        patterns.add(new PrivacyPattern("Credit Card (Dummy)", "\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9]{2})[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})\\b"));
//    }
//
//    static class PrivacyPattern {
//        String name;
//        String regex;
//
//        public PrivacyPattern(String name, String regex) {
//            this.name = name;
//            this.regex = regex;
//        }
//    }
//
//    public static void main(String[] args) {
//        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("--- PDF/Text 파일 개인정보 탐지 솔루션 시작 ---");
//        System.out.println("탐지할 파일 경로를 입력하세요. (종료: 'exit' 또는 'quit')");
//
//        while (true) {
//            System.out.print("\n파일 경로 입력 > ");
//            String filePathString = scanner.nextLine();
//
//            if (filePathString.equalsIgnoreCase("exit") || filePathString.equalsIgnoreCase("quit")) {
//                System.out.println("프로그램을 종료합니다...");
//                break;
//            }
//
//            Path filePath = Paths.get(filePathString);
//            String fileContent = "";
//
//            try {
//                // 파일 존재 여부 및 읽기 권한 확인
//                if (!Files.exists(filePath) || !Files.isReadable(filePath)) {
//                    System.err.println("오류: 파일을 찾을 수 없거나 읽을 수 없습니다 - " + filePathString);
//                    System.out.println("정확한 파일 경로를 다시 입력해주세요.");
//                    continue;
//                }
//
//                String mimeType = Files.probeContentType(filePath); // 파일의 MIME 타입 추정
//
//                // MIME 타입이 PDF인 경우 Tika 사용, 아니면 일반 텍스트로 읽기 시도
//                if (mimeType != null && mimeType.equals("application/pdf")) {
//                    // Tika 지원하는 파일 포멧은 1000개가 넘는다 => 바꾸면, 더 많은 파일 포멧 탐지 가능
//
//                    System.out.println("PDF 파일 감지: '" + filePathString + "'. Tika를 사용하여 텍스트 추출 중...");
//                    try (InputStream input = Files.newInputStream(filePath)) {
//                        BodyContentHandler handler = new BodyContentHandler(-1); // -1은 메모리 제한 없음
//                        Metadata metadata = new Metadata();
//                        ParseContext context = new ParseContext();
//
//                        AutoDetectParser parser = new AutoDetectParser();
//                        parser.parse(input, handler, metadata, context);
//                        fileContent = handler.toString();
//                        System.out.println("PDF 텍스트 추출 완료. (추출된 텍스트 크기: " + fileContent.length() + " 바이트)");
//                        System.out.println("\n[추출된 텍스트 미리보기]:\n" + fileContent); // 콘솔 출력
//                        Files.write(Paths.get("extracted_text.txt"), fileContent.getBytes()); // 파일 저장
//                        System.out.println("📄 추출된 텍스트가 'extracted_text.txt'에 저장되었습니다.");
//
//                    } catch (IOException | TikaException | SAXException e) {
//                        System.err.println("오류: PDF 텍스트 추출 중 문제 발생 - " + e.getMessage());
//                        if (e.getCause() != null) {
//                            System.err.println("  원인: " + e.getCause().getMessage());
//                        }
//                        System.out.println("다른 파일 경로를 입력해주세요.");
//                        continue;
//                    }
//                } else {
//                    System.out.println("일반 텍스트 파일 또는 알 수 없는 파일 형식 감지: '" + filePathString + "'. 일반 텍스트로 읽기 시도...");
//                    fileContent = new String(Files.readAllBytes(filePath)); // 모든 바이트를 읽어 문자열로 변환 (큰 파일에 주의)
//                    // fileContent = Files.readAllLines(filePath).stream().collect(Collectors.joining("\n")); // 기존 방식도 가능
//                    System.out.println("파일 내용 읽기 완료. (읽은 텍스트 크기: " + fileContent.length() + " 바이트)");
//                }
//
//                if (fileContent.trim().isEmpty()) {
//                    System.out.println("파일에서 추출된 텍스트가 비어 있습니다. 개인정보를 탐지할 수 없습니다.");
//                    continue;
//                }
//
//            } catch (IOException e) {
//                System.err.println("오류: 파일을 읽는 중 예기치 않은 문제 발생 - " + e.getMessage());
//                System.out.println("정확한 파일 경로를 다시 입력해주세요.");
//                continue;
//            }
//
//            // 모든 패턴 탐지 작업을 ExecutorService에 제출
//            List<Future<List<String>>> futures = new ArrayList<>();
//            for (PrivacyPattern p : patterns) {
//                Callable<List<String>> detectorTask = new PrivacyDetector(fileContent, p.name, p.regex);
//                futures.add(executorService.submit(detectorTask));
//            }
//
//            // 모든 작업의 결과 취합 및 출력
//            System.out.println("\n--- 탐지 결과 ---");
//            boolean foundAny = false;
//            for (int i = 0; i < futures.size(); i++) {
//                PrivacyPattern p = patterns.get(i);
//                Future<List<String>> future = futures.get(i);
//                try {
//                    List<String> foundItems = future.get(); // 작업 완료까지 대기 후 결과 가져오기
//                    if (!foundItems.isEmpty()) {
//                        System.out.println("✅ " + p.name + " 탐지됨: " + foundItems);
//                        foundAny = true;
//                    } else {
//                        System.out.println("❌ " + p.name + ": 탐지되지 않음");
//                    }
//                } catch (InterruptedException | ExecutionException e) {
//                    System.err.println("🚨 오류 발생 (" + p.name + "): " + e.getMessage());
//                    if (e.getCause() != null) {
//                        System.err.println("  상세: " + e.getCause().getMessage());
//                    }
//                }
//            }
//
//            if (!foundAny) {
//                System.out.println("어떤 개인정보도 탐지되지 않았습니다.");
//            }
//        }
//
//        // 프로그램 종료 시 ExecutorService 종료
//        executorService.shutdown();
//        try {
//            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) { // 대기 시간 10초로 늘림
//                executorService.shutdownNow();
//                System.out.println("스레드 풀이 강제 종료되었습니다. (일부 작업 미완료)");
//            } else {
//                System.out.println("스레드 풀이 정상적으로 종료되었습니다.");
//            }
//        } catch (InterruptedException e) {
//            executorService.shutdownNow();
//            Thread.currentThread().interrupt();
//            System.err.println("스레드 풀 종료 중 인터럽트 발생!");
//        }
//
//        scanner.close();
//        System.out.println("솔루션이 종료되었습니다.");
//    }
//}