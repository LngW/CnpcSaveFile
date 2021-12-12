package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class JsonExceptionClassVisitor extends ClassVisitor {
    public JsonExceptionClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "(Lcom/github/mrmks/mc/json/JsonUtil$JsonException;)V", null, null);

        mv.visitCode();
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/mrmks/mc/json/JsonUtil$JsonException", "getMessage", "()Ljava/lang/String;", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", "<init>", "(Ljava/lang/String;Ljava/lang/Throwable;)V", false);
        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitInsn(RETURN);
        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitLocalVariable("this", "Lnoppes/npcs/util/NBTJsonUtil$JsonException;", null, label0, label2, 0);
        mv.visitLocalVariable("e", "Lcom/github/mrmks/mc/json/JsonUtil$JsonException;", null, label0, label2, 1);
        mv.visitMaxs(3, 2);
        mv.visitEnd();

        super.visitEnd();
    }
}
