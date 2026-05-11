package com.demo.monitor.core.trace.exporter;

import com.demo.monitor.core.trace.exporter.flame.FlameNode;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlameGraphExporter {
    public static Map<String, Object> export(String traceId, FlameNode root) {

        Map<String, Object> result = new HashMap<>();

        result.put("traceId", traceId);
        result.put("totalCost", root.getCost());

        result.put("nodes", convert(root));

        return result;
    }

    private static List<Map<String, Object>> convert(FlameNode node) {

        List<Map<String, Object>> list = new ArrayList<>();

        traverse(node, list);

        return list;
    }

    private static void traverse(FlameNode node,
                                 List<Map<String, Object>> list) {

        Map<String, Object> map = new HashMap<>();

        map.put("name", node.getMethod());
        map.put("x", node.getOffset());
        map.put("y", node.getDepth());
        map.put("width", node.getCost());

        // self cost（关键）
        long childCost = node.getChildren().stream()
                .mapToLong(FlameNode::getCost)
                .sum();

        map.put("selfCost", node.getCost() - childCost);

        list.add(map);

        for (FlameNode child : node.getChildren()) {
            traverse(child, list);
        }
    }
}
