package com.demo.agent.trace.model;

import java.util.ArrayList;
import java.util.List;

public class SpanNode {

    private String spanId;
    private String parentSpanId;
    private String method;
    private long cost;
    public List<SpanNode> children = new ArrayList<>();

    public static SpanNode from(Span span) {
        SpanNode node = new SpanNode();
        node.spanId = span.getSpanId();
        node.parentSpanId = span.getParentSpanId();
        node.method = span.getMethod();
        node.cost = span.getCost();
        return node;
    }

    public String getSpanId() {
        return spanId;
    }

    public String getParentSpanId() {
        return parentSpanId;
    }

    public String getMethod() {
        return method;
    }

    public long getCost() {
        return cost;
    }

    public List<SpanNode> getChildren() {
        return children;
    }
}