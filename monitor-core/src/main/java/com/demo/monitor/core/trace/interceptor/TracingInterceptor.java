package com.demo.monitor.core.trace.interceptor;

import com.demo.monitor.core.trace.manager.TraceManager;
import com.demo.monitor.core.model.Span;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.RuntimeType;
import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.lang.reflect.Method;
import java.util.concurrent.Callable;

/**
 * TraceInterceptor:
 * 方法增强拦截器
 * <p>
 * ByteBuddy 会在目标方法执行时：
 * 自动调用 intercept()
 */
public class TracingInterceptor {

    /**
     * @RuntimeType: 允许兼容各种返回值类型
     */
    @RuntimeType
    public static Object intercept(

            /**
             * 原始方法
             */
            @SuperCall Callable<?> callable,

            /**
             * 当前方法
             */
            @Origin Method method,

            /**
             * 方法参数
             */
            @AllArguments Object[] args

    ) throws Exception {

        // ==========================================
        // 方法进入
        // ==========================================
        Span span = TraceManager.startSpan(method.getName());
        // 可以加 tag
        span.addTag("class", method.getDeclaringClass().getName());
        span.addTag("method", method.getName());

        try {
            // 执行原始方法
            return callable.call();
        } catch (Throwable e) {

            // ======================================
            // 记录异常链路
            // ======================================

//            TraceManager.error(e);
            // Agent 不改变业务行为
            TraceManager.finishSpan(e);

        }
        return null;
    }
}