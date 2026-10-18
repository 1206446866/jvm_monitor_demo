package com.demo.monitor.core.trace.exporter;

import com.demo.monitor.core.trace.exporter.flame.FlameNode;
import com.demo.monitor.core.model.SpanTimings;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FlameGraphExporter {
    public static Map<String, Object> export(String traceId, FlameNode root) {

        Map<String, Object> result = new HashMap<>();

        result.put("traceId", traceId);
        result.put("timeUnit", "ns");
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
        List<long[]> intervals = new ArrayList<>();
        for (FlameNode child : node.getChildren()) {
            intervals.add(new long[]{child.getStartTime(), child.getStartTime() + child.getCost()});
        }
        map.put("selfCost", SpanTimings.selfCost(node.getStartTime(), node.getCost(), intervals));

        list.add(map);

        for (FlameNode child : node.getChildren()) {
            traverse(child, list);
        }
    }
}
