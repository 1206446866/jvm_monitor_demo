package com.demo.monitor.core.analysis;

import com.demo.monitor.core.model.Span;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class SlowSpanDetector {
    public static final long THRESHOLD_NANOS = TimeUnit.MILLISECONDS.toNanos(100);

    public static boolean isSlow(Span span) {
        return span.getCost() > THRESHOLD_NANOS;
    }

    public static List<Span> detect(List<Span> spans) {
        return spans.stream().filter(SlowSpanDetector::isSlow).toList();
    }
}
