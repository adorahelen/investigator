package hycu.investigator.schdule.single;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;

@Configuration
@EnableScheduling  // 스케줄링 기능 활성화
public class ScheduleConfig {
}


//@EnableScheduling : 반드시 한 곳에 선언 필요
//=> 기본 스레드 방식
//  * @Scheduled는 싱글 스레드로 실행됩니다
//=> 병렬 실행하려면
//  * @Async와 @EnableAsync를 추가로 사용해야 합니다
