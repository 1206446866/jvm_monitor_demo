package com.demo.agent.asm;

import com.demo.agent.storage.MetricStore;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Type;
import org.objectweb.asm.commons.AdviceAdapter;

public class MethodTimeAdviceAdapter extends AdviceAdapter {

    private final String methodKey;
    private int startTimeVar;

    protected MethodTimeAdviceAdapter(int api, MethodVisitor mv, int access, String name, String desc, String className) {
        super(api, mv, access, name, desc);
        this.methodKey = className + "#" + name + desc;
    }

    @Override
    protected void onMethodEnter() {
        // long start = System.nanoTime();
        invokeStatic(Type.getType(System.class), new org.objectweb.asm.commons.Method("nanoTime", "()J"));

        startTimeVar = newLocal(org.objectweb.asm.Type.LONG_TYPE);
        storeLocal(startTimeVar);
    }

    @Override
    protected void onMethodExit(int opcode) {

        // long cost = System.nanoTime() - start
        invokeStatic(Type.getType(System.class), new org.objectweb.asm.commons.Method("nanoTime", "()J"));

        loadLocal(startTimeVar);
        math(SUB, org.objectweb.asm.Type.LONG_TYPE);

        // 调用 MetricStore.record(method, cost)
        push(methodKey);

        invokeStatic(Type.getType(MetricStore.class), new org.objectweb.asm.commons.Method("record", "(Ljava/lang/String;J)V"));
    }
}