package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class DataDisplayClassVisitor extends ClassVisitor {
    public DataDisplayClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("loadProfile".equals(name)) {
            return null;
        } else {
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "loadProfile", "()V", null, null);
        mv.visitVarInsn(Opcodes.ALOAD, 0);
        mv.visitFieldInsn(Opcodes.GETFIELD, "noppes/npcs/entity/data/DataDisplay", "npc", "Lnoppes/npcs/entity/EntityNPCInterface;");
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, "com/github/mrmks/mc/injector/InjectorDataDisplay", "loadProfile", "(Lnoppes/npcs/entity/EntityNPCInterface;)V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(2, 1);
        mv.visitEnd();
        super.visitEnd();
    }
}
