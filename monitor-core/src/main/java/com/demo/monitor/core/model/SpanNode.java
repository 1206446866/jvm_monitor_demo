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
        this.method = SpanNames.method(span);
        this.startTime = span.getStartTime();
        this.cost = span.getCost();
    }

    public static SpanNode from(Span span) {
        return new SpanNode(span);
    }

    public static SpanNode forest(List<SpanNode> roots) {
        SpanNode node = new SpanNode();
        node.spanId = "virtual-root";
        node.method = "[trace]";
        node.startTime = roots.stream().mapToLong(SpanNode::getStartTime).min().orElse(0);
        node.cost = roots.stream().mapToLong(r -> r.startTime + r.cost).max()
                .orElse(node.startTime) - node.startTime;
        node.children.addAll(roots);
        return node;
    }

    public long getSelfCost() {
        List<long[]> intervals = new ArrayList<>();
        for (SpanNode child : children) {
            intervals.add(new long[]{child.startTime, child.startTime + child.cost});
        }
        return SpanTimings.selfCost(startTime, cost, intervals);
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
