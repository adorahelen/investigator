package hycu.investigator.async.controller;

import hycu.investigator.async.service.MyAsyncService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class MyController {

    private final MyAsyncService myAsyncService;

    public MyController(MyAsyncService myAsyncService) {
        this.myAsyncService = myAsyncService;
    }

    @GetMapping("/start")
    public String startAsync() {
        myAsyncService.doAsyncTask();  // 비동기 호출
        return "비동기 작업 시작됨!";
    }
}