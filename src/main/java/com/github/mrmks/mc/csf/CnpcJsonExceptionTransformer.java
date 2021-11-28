package com.github.mrmks.mc.csf;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.RETURN;

public class CnpcJsonExceptionTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (!"noppes.npcs.util.NBTJsonUtil$JsonException".equals(name)) return basicClass;

        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        MethodNode mn = new MethodNode(ACC_PUBLIC, "<init>", "(Ljava/lang/String;Lcom/github/mrmks/mc/json/JsonToken;)V", null, null);
        mn.visitCode();
        Label label0 = new Label();
        mn.visitLabel(label0);
        mn.visitVarInsn(ALOAD, 0);
        mn.visitTypeInsn(NEW, "java/lang/StringBuilder");
        mn.visitInsn(DUP);
        mn.visitMethodInsn(INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
        mn.visitVarInsn(ALOAD, 1);
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mn.visitLdcInsn(": ");
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mn.visitVarInsn(ALOAD, 2);
        mn.visitMethodInsn(INVOKEVIRTUAL, "com/github/mrmks/mc/json/JsonToken", "curPosMessage", "()Ljava/lang/String;", false);
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
        mn.visitMethodInsn(INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
        mn.visitMethodInsn(INVOKESPECIAL, "java/lang/Exception", "<init>", "(Ljava/lang/String;)V", false);
        Label label1 = new Label();
        mn.visitLabel(label1);
        mn.visitInsn(RETURN);
        Label label2 = new Label();
        mn.visitLabel(label2);
        mn.visitLocalVariable("this", "Lnoppes/npcs/util/NBTJsonUtil$JsonException;", null, label0, label2, 0);
        mn.visitLocalVariable("message", "Ljava/lang/String;", null, label0, label2, 1);
        mn.visitLocalVariable("token", "Lcom/github/mrmks/mc/json/JsonToken;", null, label0, label2, 2);
        mn.visitMaxs(3, 3);
        mn.visitEnd();

        cn.methods.add(mn);

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);

        FMLLog.log.warn("[CnpcSaveFile] Transformed: noppes.npcs.util.NBTJsonUtil$JsonException");

        return DumpHelper.saveDump("noppes.npcs.util.NBTJsonUtil$JsonException", cw.toByteArray());
    }
}
