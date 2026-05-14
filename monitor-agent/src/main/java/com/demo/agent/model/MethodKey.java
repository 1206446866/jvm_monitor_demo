package com.demo.agent.model;

public class MethodKey {

    private final String className;
    private final String methodName;
    private final String desc;

    public MethodKey(String className, String methodName, String desc) {
        this.className = className;
        this.methodName = methodName;
        this.desc = desc;
    }

    public String key() {
        return className + "#" + methodName + desc;
    }
}