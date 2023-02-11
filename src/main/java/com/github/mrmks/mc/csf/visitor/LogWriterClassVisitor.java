package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LogWriterClassVisitor extends ClassVisitor {
    public LogWriterClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visitEnd() {

        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC | Opcodes.ACC_STATIC, "throwing", "(Ljava/lang/Object;Ljava/lang/Throwable;)V", null, null);

        mv.visitFieldInsn(Opcodes.GETSTATIC, "noppes/npcs/LogWriter", "logger", "Ljava/util/logging/Logger;");
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/util/logging/Level", "SEVERE", "Ljava/util/logging/Level;");
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Object", "toString", "()Ljava/lang/String;", false);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/logging/Logger", "log", "(Ljava/util/logging/Level;Ljava/lang/String;)V", false);

        mv.visitFieldInsn(Opcodes.GETSTATIC, "noppes/npcs/LogWriter", "logger", "Ljava/util/logging/Logger;");
        mv.visitFieldInsn(Opcodes.GETSTATIC, "java/util/logging/Level", "SEVERE", "Ljava/util/logging/Level;");
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/Throwable", "getMessage", "()Ljava/lang/String;", false);
        mv.visitVarInsn(Opcodes.ALOAD, 1);
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/logging/Logger", "log", "(Ljava/util/logging/Level;Ljava/lang/String;Ljava/lang/Throwable;)V", false);

        mv.visitFieldInsn(Opcodes.GETSTATIC, "noppes/npcs/LogWriter", "handler", "Ljava/util/logging/Handler;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/util/logging/Handler", "flush", "()V", false);

        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(4, 2);

        super.visitEnd();
    }
}
