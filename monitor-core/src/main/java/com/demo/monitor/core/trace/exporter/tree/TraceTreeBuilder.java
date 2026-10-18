package com.demo.monitor.core.trace.exporter.tree;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNode;

import java.util.*;

/** 乱序、缺失父节点及多根调用链均可构建；异常环不会进入输出树。 */
public class TraceTreeBuilder {
    public static SpanNode build(List<Span> spans) {
        if (spans == null || spans.isEmpty()) return null;
        Map<String, SpanNode> nodes = new LinkedHashMap<>();
        for (Span span : spans) nodes.put(span.getSpanId(), new SpanNode(span));
        List<SpanNode> roots = new ArrayList<>();
        for (SpanNode node : nodes.values()) {
            SpanNode parent = nodes.get(node.getParentSpanId());
            if (parent == null || createsCycle(node, parent, nodes)) {
                roots.add(node);
            } else {
                parent.addChild(node);
            }
        }
        SpanNode root = roots.size() == 1 ? roots.get(0) : SpanNode.forest(roots);
        sortAndSetDepth(root, 0);
        return root;
    }

    private static boolean createsCycle(SpanNode node, SpanNode parent, Map<String, SpanNode> nodes) {
        Set<String> visited = new HashSet<>();
        while (parent != null) {
            if (parent == node || !visited.add(parent.getSpanId())) return true;
            parent = nodes.get(parent.getParentSpanId());
        }
        return false;
    }

    private static void sortAndSetDepth(SpanNode node, int depth) {
        node.setDepth(depth);
        node.getChildren().sort(Comparator.comparingLong(SpanNode::getStartTime)
                .thenComparing(SpanNode::getSpanId));
        for (SpanNode child : node.getChildren()) sortAndSetDepth(child, depth + 1);
    }
}
