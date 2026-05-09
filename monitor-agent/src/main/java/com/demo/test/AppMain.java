package com.demo.test;

import com.demo.app.UserService;

public class AppMain {

    public static void main(String[] args)
            throws Exception {

        UserService service =
                new UserService();

        service.login();

        service.queryUser();
    }
}