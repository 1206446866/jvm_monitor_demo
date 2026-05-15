package com.demo.app;


public class UserService {
    public void login() throws Exception {

        Thread.sleep(100);

        System.out.println("login success");
    }

    public void queryUser()
            throws Exception {

        Thread.sleep(100);

        queryDb();
    }

    public void queryDb()
            throws Exception {

        Thread.sleep(50);
    }
}