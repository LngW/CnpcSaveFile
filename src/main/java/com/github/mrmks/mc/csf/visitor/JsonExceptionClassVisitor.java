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
        MethodVisitor mv = cv.visitMethod(ACC_PUBLIC, "<init>", "(Ljava/lang/String;Lcom/github/mrmks/mc/json/JsonToken;)V", null, null);

        mv.visitCode();
        Label label0 = new Label();
        mv.visitLabel(label0);
        mv.visitVarInsn(ALOAD, 0);
        mv.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mv.visitInsn(DUP);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mv.visitVarInsn(ALOAD, 1);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitLdcInsn(": ");
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitVarInsn(ALOAD, 2);
        mv.visitMethodInsn(INVOKEVIRTUAL, "com/github/mrmks/mc/json/JsonToken", "curPosMessage", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", "<init>", "(Ljava/lang/String;)V", false);
        Label label1 = new Label();
        mv.visitLabel(label1);
        mv.visitInsn(RETURN);
        Label label2 = new Label();
        mv.visitLabel(label2);
        mv.visitLocalVariable("this", "Lnoppes/npcs/util/NBTJsonUtil$JsonException;", null, label0, label2, 0);
        mv.visitLocalVariable("message", "Ljava/lang/String;", null, label0, label2, 1);
        mv.visitLocalVariable("token", "Lcom/github/mrmks/mc/json/JsonToken;", null, label0, label2, 2);
        mv.visitMaxs(3, 3);
        mv.visitEnd();

        super.visitEnd();
    }
}
