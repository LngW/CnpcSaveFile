package com.github.mrmks.mc.csf;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;

import static org.objectweb.asm.Opcodes.*;

public class CnpcSaveFileTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (!"noppes.npcs.util.NBTJsonUtil".equals(transformedName)) return basicClass;

        ClassReader cr = new ClassReader(basicClass);
        ClassNode cn = new ClassNode();
        cr.accept(cn, 0);

        // modify methods
        for (MethodNode mn : cn.methods) {
            if (mn.name.equals("SaveFile") && mn.desc.equals("(Ljava/io/File;Lnet/minecraft/nbt/NBTTagCompound;)V")) {
                mn.localVariables.clear();
                mn.tryCatchBlocks.clear();
                mn.instructions.clear();

                mn.visitCode();
                Label label0 = new Label();
                mn.visitLabel(label0);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitVarInsn(ALOAD, 1);
                mn.visitMethodInsn(INVOKESTATIC, "com/github/mrmks/mc/json/JsonUtil", "SaveFile", "(Ljava/io/File;Lnet/minecraft/nbt/NBTTagCompound;)V", false);
                mn.visitInsn(RETURN);
                Label label1 = new Label();
                mn.visitLabel(label1);
                mn.visitLocalVariable("file", "Ljava/io/File;", null, label0, label1, 0);
                mn.visitLocalVariable("tag", "Lnet/minecraft/nbt/NBTTagCompound;", null, label0, label1, 1);
                mn.visitMaxs(2,2);
                mn.visitEnd();
            }
            else if (mn.name.equals("Convert") && mn.desc.equals("(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;")) {
                mn.instructions.clear();
                mn.tryCatchBlocks.clear();
                mn.localVariables.clear();

                mn.visitCode();
                Label label0 = new Label();
                mn.visitLabel(label0);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(INVOKESTATIC, "com/github/mrmks/mc/json/JsonUtil","Convert", "(Ljava/lang/String;)Lnet/minecraft/nbt/NBTTagCompound;", false);
                mn.visitInsn(ARETURN);
                Label label1 = new Label();
                mn.visitLabel(label1);
                mn.visitLocalVariable("json", "Ljava/lang/String;", null, label0, label1, 0);
                mn.visitMaxs(1, 1);
                mn.visitEnd();
            }
            else if (mn.name.equals("LoadFile") && mn.desc.equals("(Ljava/io/File;)Lnet/minecraft/nbt/NBTTagCompound;")) {
                mn.localVariables.clear();
                mn.instructions.clear();
                mn.tryCatchBlocks.clear();

                mn.visitCode();
                Label label0 = new Label();
                mn.visitLabel(label0);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(INVOKESTATIC, "com/github/mrmks/mc/json/JsonUtil", "LoadFile", "(Ljava/io/File;)Lnet/minecraft/nbt/NBTTagCompound;", false);
                mn.visitInsn(ARETURN);
                Label label1 = new Label();
                mn.visitLabel(label1);
                mn.visitLocalVariable("file", "Ljava/io/File;",null,label0, label1, 0);
                mn.visitMaxs(1,1);
                mn.visitEnd();
            }
        }

        ClassWriter cw = new ClassWriter(0);
        cn.accept(cw);

        FMLLog.log.warn("[CnpcSaveFile] Transformed: noppes.npcs.util.NBTJsonUtil");

        return DumpHelper.saveDump("noppes.npcs.util.NBTJsonUtil", cw.toByteArray());
    }

}
