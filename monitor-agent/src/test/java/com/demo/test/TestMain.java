package com.demo.test;

public class TestMain {

    public static void main(String[] args) throws Exception {

        UserService userService = new UserService();

        while (true) {

            userService.login();

            Thread.sleep(1000);
        }
    }
}