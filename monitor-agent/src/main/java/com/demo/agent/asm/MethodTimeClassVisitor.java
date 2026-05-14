package com.demo.agent.asm;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class MethodTimeClassVisitor extends ClassVisitor {

    private String className;

    public MethodTimeClassVisitor(ClassVisitor cv) {
        super(Opcodes.ASM9, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name;
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (mv == null) return null;

        if (name.equals("<init>") || name.equals("<clinit>")) {
            return mv;
        }

        return new MethodTimeAdviceAdapter(Opcodes.ASM9, mv, access, name, desc, className);
    }
}