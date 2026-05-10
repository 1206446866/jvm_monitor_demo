package com.demo.agent.trace.builder;

import com.demo.agent.trace.model.Span;
import com.demo.agent.trace.model.SpanNode;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TraceTreeBuilder {

    public static SpanNode build(List<Span> spans) {

        Map<String, SpanNode> map = new HashMap<>();
        SpanNode root = null;

        // 1. convert all spans → nodes
        for (Span span : spans) {
            map.put(span.getSpanId(), SpanNode.from(span));
        }

        // 2. build tree
        for (Span span : spans) {

            SpanNode node = map.get(span.getSpanId());

            if (span.getParentSpanId() == null) {
                root = node;
            } else {
                SpanNode parent = map.get(span.getParentSpanId());
                if (parent != null) {
                    parent.children.add(node);
                }
            }
        }

        return root;
    }
}
