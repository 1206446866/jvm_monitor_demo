package com.demo.monitor.server.controller;

import com.demo.monitor.core.analysis.TraceSpanStorage;
import com.demo.monitor.core.model.Span;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
public class TraceController {

    @GetMapping("/api/trace/{traceId}")
    public List<Span> trace(@PathVariable String traceId) {
        return TraceSpanStorage.getByTraceId(traceId);
    }
}