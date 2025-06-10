//package hycu.investigator.async.service;
//
//import org.springframework.scheduling.annotation.Async;
//import org.springframework.stereotype.Service;
//
//import java.util.concurrent.CompletableFuture;
//
//@Service
//public class MyAsyncService {
//
//    // 어싱크의 경우, 반환형 타입이 아래와 같다.
//    // 01. void = 결과를 기다리지 않음
//    // 02. Future<T> = 작업이 완료될 때 결과를 받을 수 있음
//    // 03. CompletableFuture<T> = Future보다 유연한 방식, 비동기 결과를 조합 가능
//    // 04. ListenableFuture<T> = Spring 전용 인터페이스, 콜백 기능 제공
//    @Async
//    public void doAsyncTask() {
//        System.out.println("비동기 작업 시작: " + Thread.currentThread().getName());
//        try {
//            Thread.sleep(3000); // 3초 대기 (예시)
//        } catch (InterruptedException e) {
//            e.printStackTrace();
//        }
//        System.out.println("비동기 작업 완료");
//    }
//
//    @Async
//    public CompletableFuture<String> getDataAsync() {
//        return CompletableFuture.completedFuture("Hello Async");
//    }
//}
//
