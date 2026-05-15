package com.demo.monitor.server.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    public void login() throws InterruptedException {
        Thread.sleep(100);
        System.out.println("login success");
    }

    public void queryUser() throws InterruptedException {
        Thread.sleep(150);
        queryDb();
    }

    public void queryDb() throws InterruptedException {
        Thread.sleep(50);
    }
}