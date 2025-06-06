package hycu.investigator.async.config;

import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
public class AsyncConfig implements AsyncConfigurer {

// [ 비동기 ]

//🔹 @Async
//	•	가장 기본적인 비동기 실행 어노테이션
//	•	메서드를 다른 스레드에서 실행하게 만듦
//	•	사용 시 @EnableAsync 설정 필요

//🔹 @EnableAsync
//•	@Async를 활성화하기 위한 어노테이션
//•	@SpringBootApplication이나 @Configuration 클래스에 선언

//🔹 보조 어노테이션들 (간접적으로 관련)
//•	@Scheduled: 스케줄링된 작업이지만 비동기처럼 보일 수 있음
//•	@Async와 함께 쓰면 스케줄링도 비동기로 실행 가능

// Q. @Async 사용 시 무조건 다중 스레드 생성이 동반되는지 -> ?

// A. 결론부터 말하자면:
// @Async는 별도의 Executor(스레드 풀)에서 작업을 실행하므로, 다중 스레드 실행이 기본 전제입니다.

//즉, @Async를 사용하는 순간 해당 메서드는 현재 요청 처리 스레드가 아닌, 다른 스레드에서 실행됩니다.
//
//🔸 기본적으로는 SimpleAsyncTaskExecutor 사용
//•	별도 설정 없을 경우 매 요청마다 새 스레드를 생성하려는 특징이 있어 성능에 부정적 영향을 줄 수 있음
//•	실무에서는 반드시 **ThreadPoolTaskExecutor**로 제한된 스레드풀을 구성하는 것이 좋음

    @Override
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(5);     // 기본 스레드 수
        executor.setMaxPoolSize(10);     // 최대 스레드 수
        executor.setQueueCapacity(100);  // 대기 큐 용량
        executor.setThreadNamePrefix("Async-");
        executor.initialize();
        return executor;
    }
    // 비추천: 무제한 스레드 생성 가능성 있음
    //executor.setTaskExecutor(new SimpleAsyncTaskExecutor());

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return (ex, method, params) -> {
            System.err.println("비동기 예외 발생: " + ex.getMessage());
        };
    }


}