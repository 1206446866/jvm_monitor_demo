package com.demo.app;

import com.demo.agent.report.ConsoleReporter;

public class AgentTest {

    public static void main(String[] args) throws Exception {

        UserService service = new UserService();
        service.login();
        service.queryUser();
        service.queryDb();

        // 打印一次
        ConsoleReporter.printTopSlowMethods(3);
    }
}