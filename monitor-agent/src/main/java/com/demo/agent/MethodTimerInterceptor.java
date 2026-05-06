package com.demo.agent;

import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

public class MethodTimerInterceptor {

    public static Object intercept(
            @SuperCall Callable<?> callable,
            @Origin Method method
    ) throws Exception {

        long start = System.nanoTime();
//        System.out.println("intercept start");
        try {
            return callable.call();
        } finally {

            long end = System.nanoTime();

            System.out.println(
                    "Method: "
                            + method.getName()
                            + " cost "
                            + (end - start) / 1_000_000
                            + " ms"
            );
        }
    }
}