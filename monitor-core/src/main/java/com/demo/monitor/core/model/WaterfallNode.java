package com.demo.monitor.core.model;

public class WaterfallNode {

    private String method;

    /**
     * 相对 trace 起点时间
     */
    private long startOffset;

    /**
     * 持续时间
     */
    private long duration;

    /**
     * 调用深度
     */
    private int depth;
}
