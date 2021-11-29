package com.github.mrmks.mc.csf;

import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class JsonUtilCreateExceptionTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (!"com.github.mrmks.mc.json.JsonUtil".equals(name)) return basicClass;

        ClassReader cr = new ClassReader(basicClass);
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = new ClassVisitorImpl(cw);

        cw.visitInnerClass("noppes/npcs/util/NBTJsonUtil$JsonException","noppes/npcs/util/NBTJsonUtil", "JsonException", ACC_PUBLIC | ACC_STATIC);

        cr.accept(cv, 0);

        return DumpHelper.saveDump("com.github.mrmks.mc.json.JsonUtil", cw.toByteArray());
    }

    private static class ClassVisitorImpl extends ClassVisitor {

        public ClassVisitorImpl(ClassVisitor cv) {
            super(ASM5, cv);
        }

        @Override
        public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
            if ("createException".equals(name)) {
                MethodVisitor mv = this.cv.visitMethod(access, name, desc, signature, exceptions);
                mv.visitCode();
                Label label0 = new Label();
                Label label1 = new Label();
                mv.visitLabel(label0);
                mv.visitTypeInsn(NEW, "noppes/npcs/util/NBTJsonUtil$JsonException");
                mv.visitInsn(DUP);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitMethodInsn(INVOKESPECIAL, "noppes/npcs/util/NBTJsonUtil$JsonException", "<init>", "(Ljava/lang/String;Lcom/github/mrmks/mc/json/JsonToken;)V", false);
                mv.visitInsn(ARETURN);
                mv.visitLabel(label1);
                mv.visitLocalVariable("msg", "Ljava/lang/String;", null, label0, label1, 0);
                mv.visitLocalVariable("token", "Lcom/github/mrmks/mc/json/JsonToken;", null, label0, label1, 1);
                mv.visitMaxs(4,2);
                mv.visitEnd();
                return null;
            }
            return super.visitMethod(access, name, desc, signature, exceptions);
        }
    }
}
