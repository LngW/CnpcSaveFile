package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ASM5;
import static org.objectweb.asm.Opcodes.INVOKESPECIAL;

public class EntityCustomNpcClassVisitor extends ClassVisitor {
    public EntityCustomNpcClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    private boolean f = false;
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!f) {
            boolean d = FMLLaunchHandler.isDeobfuscatedEnvironment();
            if ((d ? "writeToNBTOptional" : "func_70039_c").equals(name)) {
                f = true;
                return new MethodVisitorImpl(d ? "writeToNBTAtomically" : "func_184198_c", name, super.visitMethod(access, name, desc, signature, exceptions));
            }
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
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
            super.visitMethodInsn(opcode, owner, opcode == INVOKESPECIAL && tar.equals(name) ? to : name, desc, itf);
        }
    }
}
