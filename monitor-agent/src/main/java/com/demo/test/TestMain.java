package com.demo.test;

public class TestMain {

    public static void main(String[] args) throws Exception {

        UserService userService = new UserService();

        for (int i = 0; i < 5; i++) {

            userService.login();

            Thread.sleep(1000);
        }
    }
}