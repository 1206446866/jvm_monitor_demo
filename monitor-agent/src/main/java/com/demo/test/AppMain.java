package com.demo.test;

import com.demo.monitor.core.trace.manager.TraceManager;

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