package com.demo.agent;

import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.ConcurrentHashMap;

public class CacheInterceptor {

    private static final Map<String, Object> CACHE = new ConcurrentHashMap<>();
    @RuntimeType
    public static Object intercept(
            @SuperCall Callable<?> callable,
            @Origin Method method,
            @AllArguments Object[] args
    ) throws Exception {

        System.out.println("👉 进入缓存拦截器: " + method.getName());

        String key = buildKey(method, args);

        if (CACHE.containsKey(key)) {
            System.out.println("[CACHE HIT] " + key);
            return CACHE.get(key);
        }

        Object result = callable.call();

        CACHE.put(key, result);

        return result;
    }

    private static String buildKey(Method method, Object[] args) {
        StringBuilder sb = new StringBuilder();
        sb.append(method.getName());

        if (args != null) {
            for (Object arg : args) {
                sb.append("#").append(arg);
            }
        }

        return sb.toString();
    }
}