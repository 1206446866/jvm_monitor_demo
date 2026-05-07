package com.demo.test;

import com.demo.agent.trace.TraceManager;

public class TraceManagerTest {

    public static void main(String[] args)
            throws Exception {

        controller();
    }

    public static void controller()
            throws Exception {

        TraceManager.enter(
                "Controller",
                "login"
        );

        Thread.sleep(30);

        service();

        TraceManager.exit();
    }

    public static void service()
            throws Exception {

        TraceManager.enter(
                "UserService",
                "queryUser"
        );

        Thread.sleep(50);

        dao();

        TraceManager.exit();
    }

    public static void dao()
            throws Exception {

        TraceManager.enter(
                "UserDao",
                "select"
        );

        Thread.sleep(20);

        TraceManager.exit();
    }
}