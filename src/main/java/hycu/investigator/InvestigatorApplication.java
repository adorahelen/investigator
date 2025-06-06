package hycu.investigator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class InvestigatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(InvestigatorApplication.class, args);
    }

}
