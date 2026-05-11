package com.demo.monitor.core.trace.exporter.flame;

import com.demo.monitor.core.model.SpanNode;

public class FlameGraphBuilder {

    public static FlameNode build(SpanNode root) {

        if (root == null) return null;

        FlameNode flameRoot = new FlameNode();
        convert(root, flameRoot, 0, root.getStartTime());

        return flameRoot;
    }

    private static void convert(
            SpanNode node,
            FlameNode flame,
            int depth,
            long baseTime
    ) {

        flame.setMethod(node.getMethod());
        flame.setStartTime(node.getStartTime());
        flame.setCost(node.getCost());
        flame.setDepth(depth);

        // 🔥 offset = 相对时间位置
        flame.setOffset(node.getStartTime() - baseTime);

        if (node.getChildren().isEmpty()) {
            return;
        }

        long currentBase = node.getStartTime();

        for (SpanNode child : node.getChildren()) {

            FlameNode childFlame = new FlameNode();
            flame.getChildren().add(childFlame);

            convert(child, childFlame, depth + 1, currentBase);
        }
    }
}