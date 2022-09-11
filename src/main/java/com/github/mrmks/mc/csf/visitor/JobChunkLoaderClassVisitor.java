package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class JobChunkLoaderClassVisitor extends ClassVisitor {
    public JobChunkLoaderClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("delete".equals(name)) {
            return new MethodVisitor(api, mv) {

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.RETURN) {
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "noppes/npcs/roles/JobChunkLoader", "reset", "()V", false);
                    }

                    super.visitInsn(opcode);
                }

                @Override
                public void visitMaxs(int maxStack, int maxLocals) {
                    super.visitMaxs(1, maxLocals);
                }
            };
        }
        return mv;
    }
}
