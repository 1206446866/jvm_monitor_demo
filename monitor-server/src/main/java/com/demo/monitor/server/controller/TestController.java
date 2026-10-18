package com.demo.monitor.server.controller;

import com.demo.monitor.server.service.UserService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    private final UserService userService;

    public TestController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/api/test-agent")
    public String testAgent() throws Exception {
        userService.login();
        userService.queryUser();
        userService.queryDb();
        return "测试调用已完成，请查看 /api/metrics/slow-methods 确认探针采集结果";
    }
}
