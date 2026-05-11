package com.demo.monitor.core.topology.model;

/**
 * 服务调用关系
 *
 * UserService -> OrderService
 */
public class ServiceRelation {

    private final String from;

    private final String to;

    private long count;

    public ServiceRelation(String from, String to) {
        this.from = from;
        this.to = to;
    }

    public synchronized void increment() {
        count++;
    }

    public String getFrom() {
        return from;
    }

    public String getTo() {
        return to;
    }

    public long getCount() {
        return count;
    }
}