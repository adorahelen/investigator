package hycu.investigator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.servlet.support.SpringBootServletInitializer;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class InvestigatorApplication  extends SpringBootServletInitializer {
    // SpringBootServletInitializer만 있으면, 외장 Tomcat에서 WAR 배포

    @Override
    protected SpringApplicationBuilder configure(SpringApplicationBuilder application) {
        return application.sources(InvestigatorApplication.class);
    }

//    public static void main(String[] args) {
//        SpringApplication.run(InvestigatorApplication.class, args);
//        System.out.println("Spring (외장 tomcat)웹 애플리케이션이 시작되었습니다. http://localhost:8080");
//
//    }

}

/*
* Spring Boot는 기본적으로 main() 메서드로 실행되며 내장 Tomcat을 사용
*  WAR로 외장 Tomcat에 배포할 때는 Servlet 3.0+ 규격의 ServletContainerInitializer 방식으로 실행되기 때문에,
*  => 이를 지원하려면 이 클래스를 상속 : 상속을 통해 Tomcat이 앱을 시작할 수 있게 해주는 Entry Point 역할
*
*  기존에 사용하던 main 함수를 통한 진입점이 필요한 case
*  01. 개발 환경에서 실행 (./gradlew bootRun, IntelliJ Run 등) : 이럴 땐 내장 톰캣 기반이므로 필요
 *  02. JAR 배포 (standalone) : java -jar로 실행할 때 진입점 필요
 * */
