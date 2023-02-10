package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.*;

public class ServerCloneControllerClassVisitor extends ClassVisitor {

    private static final String INJECTOR = "com/github/mrmks/mc/injector/InjectorCloneController";

    public ServerCloneControllerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("getCloneData".equals(name)) {
            mv = new MethodVisitor(api, mv) {
                @Override
                public void visitCode() {
                    super.visitCode();
                    Label label0 = new Label();
                    Label label1 = new Label();

                    super.visitVarInsn(Opcodes.ALOAD, 2);
                    super.visitVarInsn(Opcodes.ILOAD, 3);
                    super.visitMethodInsn(Opcodes.INVOKESTATIC, INJECTOR, "getCache", "(Ljava/lang/String;I)Lnet/minecraft/nbt/NBTTagCompound;", false);

                    super.visitLabel(label0);
                    super.visitVarInsn(Opcodes.ASTORE, 5);
                    super.visitVarInsn(Opcodes.ALOAD, 5);
                    super.visitJumpInsn(Opcodes.IFNULL, label1);

                    super.visitVarInsn(Opcodes.ALOAD, 5);
                    super.visitInsn(Opcodes.ARETURN);

                    super.visitLabel(label1);
                    super.visitFrame(Opcodes.F_SAME, 0, null, 0, null);

                    super.visitLocalVariable("ret", "Lnet/minecraft/nbt/NBTTagCompound;", null, label0, label1, 5);
                }

                @Override
                public void visitInsn(int opcode) {
                    if (opcode == Opcodes.ARETURN) {
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ALOAD, 2);
                        super.visitInsn(Opcodes.SWAP);
                        super.visitVarInsn(Opcodes.ILOAD, 3);
                        super.visitInsn(Opcodes.SWAP);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, INJECTOR, "setCache", "(Ljava/lang/String;ILnet/minecraft/nbt/NBTTagCompound;)V", false);
                    }
                    super.visitInsn(opcode);
                }
            };
        } else if ("saveClone".equals(name)) {
            mv = new MethodVisitor(api, mv) {
                @Override
                public void visitCode() {
                    super.visitCode();

                    super.visitVarInsn(Opcodes.ALOAD, 2);
                    super.visitVarInsn(Opcodes.ILOAD, 1);
                    super.visitVarInsn(Opcodes.ALOAD, 3);

                    super.visitMethodInsn(Opcodes.INVOKESTATIC, INJECTOR, "setCache", "(Ljava/lang/String;ILnet/minecraft/nbt/NBTTagCompound;)V", false);
                }
            };
        } else if ("removeClone".equals(name)) {
            mv = new MethodVisitor(api, mv) {
                @Override
                public void visitCode() {
                    super.visitCode();

                    super.visitVarInsn(Opcodes.ALOAD, 1);
                    super.visitVarInsn(Opcodes.ILOAD, 2);
                    super.visitInsn(Opcodes.ACONST_NULL);

                    super.visitMethodInsn(Opcodes.INVOKESTATIC, INJECTOR, "setCache", "(Ljava/lang/String;ILnet/minecraft/nbt/NBTTagCompound;)V", false);
                }
            };
        }
        return mv;
    }

    @Override
    public void visitEnd() {

        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "flushCaches", "()V", null, null);
        mv.visitCode();
        mv.visitMethodInsn(Opcodes.INVOKESTATIC, INJECTOR, "flushCaches", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(0, 1);
        mv.visitEnd();

        super.visitEnd();
    }
}
