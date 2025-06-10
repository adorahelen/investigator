package hycu.investigator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class InvestigatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestigatorApplication.class, args);
        System.out.println("Spring Boot 웹 애플리케이션이 시작되었습니다. http://localhost:8080");

    }

}
