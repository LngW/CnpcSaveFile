package com.github.mrmks.mc.csf;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.ASM5;

public class CnpcWriteNBTOptional implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!"noppes.npcs.entity.EntityCustomNpc".equals(name)) return basicClass;

        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new ClassVisitor(ASM5, cw) {
            private boolean f = false;
            @Override
            public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
                boolean t;
                if (!f && ((t = "func_70039_c".equals(name)) || "writeToNBTOptional".equals(name))) {
                    f = true;
                    return new MethodVisitorImpl(t ? "func_184198_c" : "writeToNBTAtomically", name, super.visitMethod(access, name, desc, signature, exceptions));
                }
                return super.visitMethod(access, name, desc, signature, exceptions);
            }
        };
        cr.accept(cv, 0);

        return cw.toByteArray();
    }

    private static class MethodVisitorImpl extends MethodVisitor {
        private final String tar, to;
        MethodVisitorImpl(String tar, String to, MethodVisitor mv) {
            super(ASM5, mv);
            this.tar = tar;
            this.to = to;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, tar.equals(name) ? to : name, desc, itf);
        }
    }
}
