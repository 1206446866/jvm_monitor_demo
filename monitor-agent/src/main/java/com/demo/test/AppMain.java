package com.demo.test;

import com.demo.agent.trace.manager.TraceManager;
import com.demo.app.UserService;

public class AppMain {

    public static void main(String[] args)
            throws Exception {

//        UserService service =
//                new UserService();
//
//        service.login();
//
//        service.queryUser();

        TraceManager.startSpan("Controller.login");

        TraceManager.startSpan("Service.query");
        TraceManager.finishSpan(null);

        TraceManager.finishSpan(null);
    }
}