//package hycu.investigator.test;
//
//import java.io.IOException;
//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.util.ArrayList;
//import java.util.List;
//import java.util.Scanner;
//import java.util.concurrent.*;
//import java.util.regex.Matcher;
//import java.util.regex.Pattern;
//import java.util.stream.Collectors;
//
//// --- 개인정보 탐지 작업을 정의하는 Callable 클래스 ---
//class PrivacyDetectorFile implements Callable<List<String>> {
//    private String text;
//    private String patternName;
//    private Pattern pattern;
//
//    public PrivacyDetectorFile(String text, String patternName, String regex) {
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
//        System.out.println(Thread.currentThread().getName() + " - Detecting " + patternName + "...");
//
//        while (matcher.find()) {
//            foundItems.add(matcher.group());
//        }
//
//        // 탐지 작업 시간 시뮬레이션 (네트워크 지연, 복잡한 연산 등)
//        Thread.sleep((long) (Math.random() * 500) + 100);
//
//        return foundItems;
//    }
//}
//
//// --- 메인 솔루션 클래스 ---
//public class FilePrivacyDetectionSolution {
//
////    01.파일 경로 입력: CMD에서 텍스트를 직접 입력받는 대신, 파일 경로를 입력받도록 변경합니다.
////    02.파일 내용 읽기: 입력받은 파일 경로를 사용하여 java.nio.file.Files.readAllLines() 메서드로 파일의 모든 라인을 읽어옵니다. 이를 하나의 큰 문자열로 합쳐 개인정보 탐지에 사용합니다.
////    03.예외 처리: 파일이 없거나 읽을 수 없을 경우 발생하는 IOException을 처리하는 로직을 추가합니다.
//            // * java.io 보다 java.nio 가 조금 더 현대적인 로직 제공
//
//
//    private static final int THREAD_POOL_SIZE = 4; // 스레드 풀 크기
//    private static ExecutorService executorService; // 스레드 풀 객체
//
//    // 개인정보 패턴 정의 (예시)
//    private static final List<PrivacyPattern> patterns = new ArrayList<>();
//
//    static {
//        // 한국 전화번호 정규식 예시: 010-1234-5678, 02-123-4567 등
//        // 참고: 정규식은 실제 환경에 따라 훨씬 더 정교해야 합니다.
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
//        // 고정 스레드 풀 초기화
//        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
//        Scanner scanner = new Scanner(System.in);
//
//        System.out.println("--- 개인정보 탐지 솔루션 시작 ---");
//        System.out.println("탐지할 파일 경로를 입력하세요. (종료: 'exit' 또는 'quit')");
//
//        while (true) {
//            System.out.print("\n파일 경로 입력 > ");
//            String filePath = scanner.nextLine();
//
//            if (filePath.equalsIgnoreCase("exit") || filePath.equalsIgnoreCase("quit")) {
//                System.out.println("프로그램을 종료합니다...");
//                break; // 루프 종료
//            }
//
//            String fileContent = "";
//            try {
//                // 파일의 모든 라인을 읽어서 하나의 문자열로 합침 (이 부분 nio 에서 제공하는 간소화된 현대적인 로직임)
//                fileContent = Files.readAllLines(Paths.get(filePath))
//                        .stream()
//                        .collect(Collectors.joining("\n")); // 각 라인을 줄바꿈으로 연결 : 읽어온 라인들을 하나의 긴 문자열로
//                System.out.println("파일 '" + filePath + "' 내용을 읽었습니다. 탐지 시작...");
//            } catch (IOException e) {
//                System.err.println("오류: 파일을 읽을 수 없습니다 - " + e.getMessage());
//                System.out.println("정확한 파일 경로를 다시 입력해주세요.");
//                continue; // 다음 루프로 이동하여 다시 파일 경로 입력 받음
//            }
//
//            // 모든 패턴 탐지 작업을 ExecutorService에 제출
//            List<Future<List<String>>> futures = new ArrayList<>();
//            for (PrivacyPattern p : patterns) {
//                // 파일 내용을 탐지할 텍스트로 전달
//                Callable<List<String>> detectorTask = new PrivacyDetector(fileContent, p.name, p.regex); // 생성 후 전달
//                futures.add(executorService.submit(detectorTask)); // 스레드에서 받아서 처리 후 future(비동기,지연 반환)
//            }
//
//            // 모든 작업의 결과 취합 및 출력
//            System.out.println("--- 탐지 결과 ---");
//            boolean foundAny = false;
//            for (int i = 0; i < futures.size(); i++) {
//                PrivacyPattern p = patterns.get(i);
//                Future<List<String>> future = futures.get(i);
//                try {
//                    List<String> foundItems = future.get(); // 작업 완료까지 대기 후 결과 가져오기
//                    if (!foundItems.isEmpty()) {
//                        System.out.println(p.name + " 탐지됨: " + foundItems);
//                        foundAny = true;
//                    } else {
//                        System.out.println(p.name + ": 탐지되지 않음");
//                    }
//                } catch (InterruptedException | ExecutionException e) {
//                    System.err.println("오류 발생 (" + p.name + "): " + e.getMessage());
//                    if (e.getCause() instanceof IllegalArgumentException) {
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
//        executorService.shutdown(); // 새 작업은 받지 않고 현재 작업 완료 대기
//        try {
//            // 모든 작업이 5초 내에 종료될 때까지 대기
//            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
//                executorService.shutdownNow(); // 5초 초과 시 강제 종료
//                System.out.println("스레드 풀이 강제 종료되었습니다.");
//            } else {
//                System.out.println("스레드 풀이 정상적으로 종료되었습니다.");
//            }
//        } catch (InterruptedException e) {
//            executorService.shutdownNow();
//            Thread.currentThread().interrupt(); // 현재 스레드의 인터럽트 상태 복원
//            System.err.println("스레드 풀 종료 중 인터럽트 발생!");
//        }
//
//        scanner.close();
//        System.out.println("솔루션이 종료되었습니다.");
//    }
//}
//// 탐지 가능 영역 및 원리 :
///*
//* 현재 제공된 자바 코드는 파일의 내용을 통째로 읽어서 하나의 큰 문자열(String)로 만든 다음, 그 문자열에 대해 정규표현식 기반의 개인정보 탐지를 수행
//* => 실질적으로 탐지 가능한 파일 포맷: 텍스트 기반 파일
//*  [원리: 파일의 내용을 "글자"로 읽어 들여서 정규표현식 패턴과 비교하는 방식이기 때문에,
//*   파일의 내용이 텍스트(문자열) 형태로 의미 있게 표현될 수 있는 경우에만 탐지가 가능]
//*
//     예시:
//        .txt (텍스트 파일)
//        .log (로그 파일)
//        .csv (쉼표로 구분된 값 파일)
//        .json (JSON 파일)
//        .xml (XML 파일)
//        .html, .css, .js (웹 관련 코드 파일)
//        .java, .py, .c, .cpp 등 (소스 코드 파일)
//        기타 평문(plain text) 형태로 저장된 모든 파일
//
//* */
//
//// 탐지 불가능 영역 및 이유 :
///*
//*  탐지가 어렵거나 불가능한 파일 포맷: 이진 파일 및 복합 문서 포맷
//원리: 이러한 파일들은 내부적으로 텍스트 외에 이미지, 서식 정보, 압축된 데이터 등 다양한 이진(binary) 데이터를 포함하고 있습니다.
//=> 단순히 Files.readAllLines()로 읽어들이면 의미 없는 특수 문자나 깨진 문자들이 섞여 들어오기 때문에,
//   정규표현식으로는 올바른 개인정보를 탐지하기 매우 어렵거나 불가능
//
//   예시:
//        .docx, .xlsx, .pptx (MS Office 문서)
//        .pdf (PDF 문서)
//        .jpg, .png, .gif (이미지 파일)
//        .mp3, .wav (오디오 파일)
//        .mp4, .avi (비디오 파일)
//        .zip, .rar (압축 파일)
//        .exe, .dll (실행 파일 및 라이브러리)
//
// * */
//
////  파일의 내용을 텍스트로 해석할 수 있다면 어떤 파일 포맷이든 입력으로 받을 수 있다.
////  =>  복합 문서나 이진 파일 내의 개인정보를 탐지하고 싶다면,
////  해당 파일 포맷을 파싱하여 텍스트만 추출해내는 별도의 **파싱 라이브러리
////  (예: Apache POI for Office, Apache PDFBox for PDF)**를 추가적으로 사용해야 합니다.
//
