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

        // ==========================================
        // 根据调用深度生成缩进
        // ==========================================

        String indent = "  ".repeat(depth);

        // ==========================================
        // 打印当前 Span
        // ==========================================

        System.out.println(
                indent
                        + span.getMethodName()
                        + " ["
                        + span.getCost()
                        + "ms]"
        );

        // ==========================================
        // 递归打印 children
        // ==========================================

        for (Span child : span.getChildren()) {

            printSpan(child, depth + 1);
        }
    }
}
