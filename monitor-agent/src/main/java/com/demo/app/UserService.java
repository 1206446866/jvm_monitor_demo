package com.demo.app;

import com.demo.monitor.core.trace.Trace;

@Trace
public class UserService {
@Trace
    public void login() throws Exception {

        Thread.sleep(100);

        System.out.println("login success");
    }

//    @Trace
    public void queryUser()
            throws Exception {

        Thread.sleep(100);

        queryDb();
    }
//    @Trace
    public void queryDb()
            throws Exception {

        Thread.sleep(50);
    }
}