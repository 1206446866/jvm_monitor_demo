package com.demo.monitor.core.analysis;

import com.demo.monitor.core.model.Span;

import java.util.ArrayList;
import java.util.List;

//慢调用检测
public class SlowSpanDetector {

    private static final long SLOW_THRESHOLD_MS = 50;

    public static List<Span> detect(List<Span> spans) {

        List<Span> slow = new ArrayList<>();

        for (Span span : spans) {

            if (span.getCost() > SLOW_THRESHOLD_MS) {
                slow.add(span);
            }
        }

        return slow;
    }
}
