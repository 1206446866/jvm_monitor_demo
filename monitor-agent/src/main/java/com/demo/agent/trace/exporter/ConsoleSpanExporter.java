package com.demo.agent.trace.exporter;


import com.demo.agent.trace.model.Span;

public class ConsoleSpanExporter implements SpanExporter {
    @Override
    public void export(Span rootSpan) {

        printSpan(rootSpan, 0);
    }

    /**
     * 递归打印 Span Tree
     */
    private void printSpan(
            Span span,
            int depth
    ) {

    }
}
