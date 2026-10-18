package com.demo.monitor.core.model;

/** 统一兼容“类名 + 方法名”和已限定的方法全名。 */
public final class SpanNames {
    private SpanNames() {}

    public static String method(Span span) {
        String method = span.getMethodName() == null ? "unknown" : span.getMethodName();
        String owner = span.getClassName();
        return owner == null || owner.isBlank() || method.startsWith(owner + ".")
                ? method : owner + "." + method;
    }

    /** 单 JVM 拓扑以业务类为节点，不将包名前缀误当成服务。 */
    public static String service(Span span) {
        if (span.getClassName() != null && !span.getClassName().isBlank()) {
            return span.getClassName();
        }
        String method = method(span);
        int separator = Math.max(method.lastIndexOf('.'), method.lastIndexOf('#'));
        return separator > 0 ? method.substring(0, separator) : "unknown";
    }
}
