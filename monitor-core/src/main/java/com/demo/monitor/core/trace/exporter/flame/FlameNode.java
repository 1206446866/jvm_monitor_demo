package com.demo.monitor.core.trace.exporter.flame;

import java.util.ArrayList;
import java.util.List;

public class FlameNode {

    private String method;

    private long startTime;

    private long cost;

    private int depth;

    private long offset; // 🔥 横坐标（关键）

    private List<FlameNode> children = new ArrayList<>();

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long startTime) {
        this.startTime = startTime;
    }

    public long getCost() {
        return cost;
    }

    public void setCost(long cost) {
        this.cost = cost;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public long getOffset() {
        return offset;
    }

    public void setOffset(long offset) {
        this.offset = offset;
    }

    public List<FlameNode> getChildren() {
        return children;
    }

    public void setChildren(List<FlameNode> children) {
        this.children = children;
    }
}