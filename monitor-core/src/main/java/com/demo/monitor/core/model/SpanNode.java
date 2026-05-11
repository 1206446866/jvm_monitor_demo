package com.demo.monitor.core.model;

import java.util.ArrayList;
import java.util.List;

public class SpanNode {

    private String spanId;
    private String parentSpanId;
    private String method;
    private long startTime;
    private long cost;
    private int depth;
    private final List<SpanNode> children = new ArrayList<>();

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public int getDepth() {
        return depth;
    }
    public SpanNode(){}

    public SpanNode(Span span) {
        this.spanId = span.getSpanId();
        this.parentSpanId = span.getParentSpanId();
        this.method = span.getMethodName();
        this.startTime = span.getStartTime();
        this.cost = span.getCost();
    }

    public static SpanNode from(Span span) {
        SpanNode node = new SpanNode();
        node.spanId = span.getSpanId();
        node.parentSpanId = span.getParentSpanId();
        node.method = span.getMethodName();
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

    public void addChild(SpanNode child) {
        this.children.add(child);
    }

    public List<SpanNode> getChildren() {
        return children;
    }

    public long getStartTime() {
        return startTime;
    }


}