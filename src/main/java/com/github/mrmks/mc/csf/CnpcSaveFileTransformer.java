package com.github.mrmks.mc.csf;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class CnpcSaveFileTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (!"noppes.npcs.util.NBTJsonUtil".equals(transformedName)) return basicClass;

        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new ClassVisitorImpl(cw);
        cr.accept(cv, 0);

        TransformHelper.transformed(name);

        return TransformHelper.saveDump(name, cw.toByteArray());
    }

    private static class ClassVisitorImpl extends ClassVisitor {
        private final boolean[] f = new boolean[]{false, false, false};
        public ClassVisitorImpl(ClassVisitor cv) {
            super(ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if (!f[0] && "LoadFile".equals(name)) {
                MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
                mv.visitCode();
                Label label0 = new Label();
                Label label1 = new Label();
                mv.visitLabel(label0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, "com/github/mrmks/mc/json/JsonUtil", "LoadFile", "(Ljava/io/File;)Lnet/minecraft/nbt/NBTTagCompound;", false);
                mv.visitInsn(ARETURN);
                mv.visitLabel(label1);
                mv.visitLocalVariable("file", "Ljava/io/File;",null,label0, label1, 0);
                mv.visitMaxs(1,1);
                mv.visitEnd();
                f[0] = true;
                return null;
            } else if (!f[1] && "SaveFile".equals(name)) {
                MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
                Label label0 = new Label();
                Label label1 = new Label();
                mv.visitCode();
                mv.visitLabel(label0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESTATIC, "com/github/mrmks/mc/json/JsonUtil", "SaveFile", "(Ljava/io/File;Lnet/minecraft/nbt/NBTTagCompound;)V", false);
                mv.visitInsn(RETURN);
                mv.visitLabel(label1);
                mv.visitLocalVariable("file", "Ljava/io/File;", null, label0, label1, 0);
                mv.visitLocalVariable("tag", "Lnet/minecraft/nbt/NBTTagCompound;", null, label0, label1, 1);
                mv.visitMaxs(2,2);
                mv.visitEnd();
                f[1] = true;
                return null;
            } else if (!f[2] && "Convert".equals(name) && "(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;".equals(desc)) {
                MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
                mv.visitCode();
                Label label0 = new Label();
                mv.visitLabel(label0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKESTATIC, "com/github/mrmks/mc/json/JsonUtil","Convert", "(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;", false);
                mv.visitInsn(ARETURN);
                Label label1 = new Label();
                mv.visitLabel(label1);
                mv.visitLocalVariable("json", "Ljava/lang/String;", null, label0, label1, 0);
                mv.visitMaxs(1, 1);
                mv.visitEnd();
                f[2] = true;
                return null;
            } else return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }

}
