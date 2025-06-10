//package hycu.investigator.schdule.multi;
//
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.scheduling.annotation.Scheduled;
//import org.springframework.stereotype.Component;
//
//
//
//
//
//@Component
//public class MyTaskAsync {
//    @Async
//    @Scheduled(fixedRate = 3000)
//    public void runParallel() throws InterruptedException {
//        System.out.println("▶ 시작: " + Thread.currentThread().getName());
//        Thread.sleep(5000); // 일부러 지연
//        System.out.println("▶ 종료: " + Thread.currentThread().getName());
//    }
//}
