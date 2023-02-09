package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ServerClassVisitor extends ClassVisitor {
    public ServerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("writeNBT".equals(name) && "(Lio/netty/buffer/ByteBuf;Lnet/minecraft/nbt/NBTTagCompound;)V".equals(desc)) {
            mv = new MethodVisitor(api, mv) {
                boolean flag = false;

                @Override
                public void visitTypeInsn(int opcode, String type) {
                    if (opcode == Opcodes.NEW && "java/util/zip/GZIPOutputStream".equals(type)) {
                        flag = true;
                    } else {
                        super.visitTypeInsn(opcode, type);
                    }
                }

                @Override
                public void visitInsn(int opcode) {
                    if (!flag) super.visitInsn(opcode);
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (flag) {
                        flag = false;
                        opcode = Opcodes.INVOKESTATIC;
                        owner = "com/github/mrmks/mc/injector/InjectorServer";
                        name = "warpGZipOutputStream";
                        desc = "(Ljava/io/OutputStream;)Ljava/io/OutputStream;";
                        itf = false;
                    }
                    super.visitMethodInsn(opcode, owner, name, desc, itf);
                }
            };
        }
        return mv;
    }
}
