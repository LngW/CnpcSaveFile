package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class NoppesUtilsServer$1ClassVisitor extends ClassVisitor {
    public NoppesUtilsServer$1ClassVisitor(int api, ClassVisitor p) {
        super(api, p);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, "net/minecraft/command/CommandSenderWrapper", interfaces);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("<init>".equals(name)) {
            return new MethodVisitor(api, mv) {

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode == Opcodes.INVOKESPECIAL && "<init>".equals(name)) {
                        super.visitInsn(Opcodes.POP);
                        super.visitVarInsn(Opcodes.ALOAD, 6);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitInsn(Opcodes.ACONST_NULL);
                        super.visitMethodInsn(opcode, "net/minecraft/command/CommandSenderWrapper", name, "(Lnet/minecraft/command/ICommandSender;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/math/BlockPos;Ljava/lang/Integer;Lnet/minecraft/entity/Entity;Ljava/lang/Boolean;)V", itf);
                    } else super.visitMethodInsn(opcode, owner, name, desc, itf);
                }

                @Override
                public void visitMaxs(int maxStack, int maxLocals) {
                    super.visitMaxs(7, maxLocals);
                }
            };
        } else return mv;
    }
}
