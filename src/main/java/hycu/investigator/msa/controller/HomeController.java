package hycu.investigator.msa.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String index() {
        System.out.println(" 외장 톰캣 + War 는 이게 없으면 아무것도 안뜨는건가? ");
        return "index"; // templates/index.html 로 렌더링
    }
}
