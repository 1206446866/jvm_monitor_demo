package com.demo.test;

public class UserService {

    public void login() throws Exception {
        doQuery();
        Thread.sleep(100);
        System.out.println("login success");
    }

    public void doQuery() throws Exception {
        Thread.sleep(50);
    }

    public String queryUser(String id) {
        System.out.println("DB query...");
        return "user-" + id;
    }
}