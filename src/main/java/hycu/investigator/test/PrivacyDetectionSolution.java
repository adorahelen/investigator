package hycu.investigator.test;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

// --- 개인정보 탐지 작업을 정의하는 Callable 클래스 ---
class PrivacyDetectorText implements Callable<List<String>> {
    private String text;
    private String patternName;
    private Pattern pattern;

    public PrivacyDetectorText(String text, String patternName, String regex) {
        this.text = text;
        this.patternName = patternName;
        this.pattern = Pattern.compile(regex); // 정규식 컴파일
    }

    @Override
    public List<String> call() throws Exception {
        List<String> foundItems = new ArrayList<>();
        Matcher matcher = pattern.matcher(text);

        System.out.println(Thread.currentThread().getName() + " - Detecting " + patternName + "...");

        while (matcher.find()) {
            foundItems.add(matcher.group());
        }

        // 탐지 작업 시간 시뮬레이션 (네트워크 지연, 복잡한 연산 등)
        Thread.sleep((long) (Math.random() * 500) + 100);

        return foundItems;
    }
}

// --- 메인 솔루션 클래스 ---
public class PrivacyDetectionSolution {

    private static final int THREAD_POOL_SIZE = 4; // 스레드 풀 크기
    private static ExecutorService executorService; // 스레드 풀 객체

    // 개인정보 패턴 정의 (예시)
    private static final List<PrivacyPattern> patterns = new ArrayList<>();

    static {
        patterns.add(new PrivacyPattern("Phone Number", "\\b\\d{3}[-.]?\\d{4}[-.]?\\d{4}\\b")); // 000-000-0000
        patterns.add(new PrivacyPattern("Email Address", "\\b[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,6}\\b"));
        patterns.add(new PrivacyPattern("SSN (Dummy)", "\\b\\d{3}-\\d{2}-\\d{4}\\b")); // 가상 주민등록번호 (미국 형식)
        patterns.add(new PrivacyPattern("Credit Card (Dummy)", "\\b(?:4[0-9]{12}(?:[0-9]{3})?|5[1-5][0-9]{14}|6(?:011|5[0-9]{2})[0-9]{12}|3[47][0-9]{13}|3(?:0[0-5]|[68][0-9])[0-9]{11}|(?:2131|1800|35\\d{3})\\d{11})\\b")); // 가상 카드번호
    }

    static class PrivacyPattern {
        String name;
        String regex;

        public PrivacyPattern(String name, String regex) {
            this.name = name;
            this.regex = regex;
        }
    }

    public static void main(String[] args) {

//        입력: 사용자로부터 표준 입력(CMD)을 통해 텍스트를 받습니다.
//        백그라운드 처리: 입력된 텍스트는 **스레드 풀(ExecutorService)**에 제출됩니다.
//        동시성 탐지: 스레드 풀의 3~4개 스레드가 각각 다른 개인정보 패턴(예: 전화번호, 이메일 주소, 주민등록번호 등)을 동시에 탐지합니다.
//        결과 취합: 각 스레드의 탐지 결과는 취합되어 사용자에게 보여집니다.
//        지속 실행: 프로그램은 사용자가 exit나 quit 같은 종료 명령어를 입력할 때까지 계속해서 텍스트 입력을 받고 처리합니다.



        // 고정 스레드 풀 초기화
        executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
        Scanner scanner = new Scanner(System.in);

        System.out.println("--- 개인정보 탐지 솔루션 시작 ---");
        System.out.println("텍스트를 입력하고 Enter를 누르세요. (종료: 'exit' 또는 'quit')");

        while (true) {
            System.out.print("\n입력 > ");
            String inputText = scanner.nextLine();

            if (inputText.equalsIgnoreCase("exit") || inputText.equalsIgnoreCase("quit")) {
                System.out.println("프로그램을 종료합니다...");
                break; // 루프 종료
            }

            // 모든 패턴 탐지 작업을 ExecutorService에 제출 : -> 병렬로 개인정보 탐지
            List<Future<List<String>>> futures = new ArrayList<>();
            for (PrivacyPattern p : patterns) {
                Callable<List<String>> detectorTask = new PrivacyDetector(inputText, p.name, p.regex);
                futures.add(executorService.submit(detectorTask)); // -> 이 작업(Callable)을 백그라운드에서 비동기적으로 실행해
            }

//            단계 : 내용
//            1
//              해당 패턴(예: 전화번호)에 대한 탐지 작업 객체를 생성PrivacyDetector는 Callable<List<String>>을 구현한 클래스
//            2
//              executorService.submit(detectorTask)로 스레드 풀에 이 작업을 제출
//            3
//              제출하면 이 작업은 백그라운드 스레드 중 하나에서 실행됨
//            4
//              이 submit()은 Future<List<String>> 객체를 반환함 → 비동기 결과를 나중에 가져올 수 있음
//            5
//              이 Future 객체들을 리스트에 모아둠 (futures.add(...))


            // 모든 작업의 결과 취합 및 출력
            System.out.println("--- 탐지 결과 ---");
            boolean foundAny = false;
            for (int i = 0; i < futures.size(); i++) {
                PrivacyPattern p = patterns.get(i);
                Future<List<String>> future = futures.get(i);
                try {
                    List<String> foundItems = future.get(); // 작업 완료까지 대기 후 결과 가져오기
                    if (!foundItems.isEmpty()) {
                        System.out.println(p.name + " 탐지됨: " + foundItems);
                        foundAny = true;
                    } else {
                        System.out.println(p.name + ": 탐지되지 않음");
                    }
                } catch (InterruptedException | ExecutionException e) {
                    System.err.println("오류 발생 (" + p.name + "): " + e.getMessage());
                    if (e.getCause() instanceof IllegalArgumentException) {
                        System.err.println("  상세: " + e.getCause().getMessage());
                    }
                }
            }

            if (!foundAny) {
                System.out.println("어떤 개인정보도 탐지되지 않았습니다.");
            }
        }

        // 프로그램 종료 시 ExecutorService 종료
        executorService.shutdown(); // 새 작업은 받지 않고 현재 작업 완료 대기
        try {
            // 모든 작업이 5초 내에 종료될 때까지 대기
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow(); // 5초 초과 시 강제 종료
                System.out.println("스레드 풀이 강제 종료되었습니다.");
            } else {
                System.out.println("스레드 풀이 정상적으로 종료되었습니다.");
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt(); // 현재 스레드의 인터럽트 상태 복원
            System.err.println("스레드 풀 종료 중 인터럽트 발생!");
        }

        scanner.close();
        System.out.println("솔루션이 종료되었습니다.");
    }
} // only text