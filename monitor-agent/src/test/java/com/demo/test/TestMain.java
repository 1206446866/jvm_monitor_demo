package com.demo.test;

import com.demo.app.UserService;

public class TestMain {

    public static void main(String[] args) throws Exception {

        com.demo.app.UserService userService = new UserService();

        while (true) {

            userService.login();

            Thread.sleep(1000);
        }
    }
}