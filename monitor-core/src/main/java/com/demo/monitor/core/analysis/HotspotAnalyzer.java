package com.demo.monitor.core.analysis;

import com.demo.monitor.core.model.Span;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HotspotAnalyzer {

    public static Map<String, Long> analyze(List<Span> spans) {

        Map<String, Long> costMap = new HashMap<>();

        for (Span span : spans) {

            String key = span.getClassName() + "." + span.getMethodName();

            costMap.put(
                    key,
                    costMap.getOrDefault(key, 0L) + span.getCost()
            );
        }

        return costMap;
    }
}
