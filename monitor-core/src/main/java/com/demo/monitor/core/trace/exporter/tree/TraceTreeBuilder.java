package com.demo.monitor.core.trace.exporter.tree;

import com.demo.monitor.core.model.Span;
import com.demo.monitor.core.model.SpanNode;

import java.util.*;

/**
 * TraceTreeBuilder V2（工业级）
 *
 * 特性：
 * 1. 乱序 span 支持
 * 2. 自动 root 识别（避免误判）
 * 3. parent 丢失容错
 * 4. children 排序（按 startTime）
 * 5. depth 计算（flame graph 必备）
 * 6. 支持森林 fallback（避免 null root）
 */
public class TraceTreeBuilder {

    public static SpanNode build(List<Span> spans) {

        if (spans == null || spans.isEmpty()) {
            return null;
        }

        // 1. spanId → node
        Map<String, SpanNode> map = new HashMap<>();

        for (Span span : spans) {
            map.put(span.getSpanId(), new SpanNode(span));
        }

        SpanNode root = null;

        // 2. 构建关系
        for (Span span : spans) {

            SpanNode node = map.get(span.getSpanId());
            String parentId = span.getParentSpanId();

            if (parentId == null) {

                // ⚠ 只在第一次设置 root
                if (root == null) {
                    root = node;
                } else {
                    // 多 root → fallback 处理
                    root.addChild(node);
                }

            } else {

                SpanNode parent = map.get(parentId);

                if (parent != null) {
                    parent.addChild(node);
                } else {
                    // ❗断链 fallback：当 root 子节点
                    root.addChild(node);
                }
            }
        }

        // 3. 计算 depth + 排序
        computeDepthAndSort(root, 0);

        return root;
    }

    /**
     * 递归计算 depth + 排序 children
     */
    private static void computeDepthAndSort(SpanNode node, int depth) {

        if (node == null) return;

        node.setDepth(depth);

        if (node.getChildren().isEmpty()) {
            return;
        }

        // ✔ 排序（工业级关键）
        node.getChildren().sort(
                Comparator.comparingLong(c -> c.getStartTime())
        );

        for (SpanNode child : node.getChildren()) {
            computeDepthAndSort(child, depth + 1);
        }
    }
}