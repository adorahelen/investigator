package hycu.investigator.test2.single;

//@Component
//public class MyTask {

    // [ 01. 고정 간격 실행 (fixedRate) ]
    // 💡 이전 작업이 끝나지 않았더라도 5초마다 새로운 실행이 시작됩니다.
//    @Scheduled(fixedRate = 5000) // 5초마다 실행 (작업 시작 시점 기준)
//    public void runEveryFiveSeconds() {
//        System.out.println("▶ fixedRate 실행: " + LocalDateTime.now());
//    }
//
//    //  [ 02. 고정 지연 실행 (fixedDelay) ]
//    // 이전 작업이 끝난 후 5초를 기다린 뒤 실행됩니다.
//    @Scheduled(fixedDelay = 5000) // 이전 작업이 끝난 후 5초 뒤 실행
//    public void runAfterDelay() {
//        System.out.println("▶ fixedDelay 실행: " + LocalDateTime.now());
//    }
//
//    //  [ 03. 크론 표현식을 사용한 스케쥴링 ]
//    @Scheduled(cron = "0 0/1 * * * *") // 매 1분마다 실행
//    public void runByCron() {
//        System.out.println("▶ cron 표현식 실행: " + LocalDateTime.now());
//    }
//}
/*

▶ fixedDelay 실행: 2025-06-10T20:37:10.462425
▶ 종료: Async-5
▶ 시작: Async-2
▶ fixedRate 실행: 2025-06-10T20:37:15.460427
▶ fixedDelay 실행: 2025-06-10T20:37:15.463212
▶ 종료: Async-1
▶ 시작: Async-3
▶ 종료: Async-2
▶ 시작: Async-4
▶ fixedRate 실행: 2025-06-10T20:37:20.460633
▶ fixedDelay 실행: 2025-06-10T20:37:20.464087
▶ 종료: Async-3


 */