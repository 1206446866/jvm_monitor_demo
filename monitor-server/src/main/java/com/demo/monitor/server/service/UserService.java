package com.demo.monitor.server.service;

import org.springframework.stereotype.Service;

@Service
public class UserService {

    private final DatabaseService databaseService;

    public UserService(DatabaseService databaseService) {
        this.databaseService = databaseService;
    }

    public void login() throws InterruptedException {
        Thread.sleep(100);
        System.out.println("login success");
    }

    public void queryUser() throws InterruptedException {
        Thread.sleep(150);
        queryDb();
    }

    public void queryDb() throws InterruptedException {
        databaseService.query();
    }
}
