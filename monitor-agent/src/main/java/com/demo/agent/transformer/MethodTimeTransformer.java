package com.demo.agent.transformer;

import com.demo.agent.asm.MethodTimeClassVisitor;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.MethodVisitor;

import java.lang.instrument.ClassFileTransformer;
import java.security.ProtectionDomain;

import static org.objectweb.asm.Opcodes.ASM9;


public class MethodTimeTransformer implements ClassFileTransformer {

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classFileBuffer) {

        if (className == null || className.startsWith("java/")) {
            return classFileBuffer;
        }

        ClassReader cr = new ClassReader(classFileBuffer);
        ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);

        ClassVisitor cv = new ClassVisitor(ASM9, cw) {

            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {

                MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

                // 跳过构造 / abstract / native
                if (name.equals("<init>") || name.equals("<clinit>")) {
                    return mv;
                }

                return new MethodTimeClassVisitor(api, mv, className, name, desc);
            }
        };

        cr.accept(cv, ClassReader.EXPAND_FRAMES);
        return cw.toByteArray();
    }
}