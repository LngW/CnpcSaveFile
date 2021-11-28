package com.github.mrmks.mc.csf;

import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.common.FMLLog;
import noppes.npcs.util.NBTJsonUtil;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.InnerClassNode;
import org.objectweb.asm.tree.MethodNode;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.LinkedList;
import java.util.List;

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
                /*
                mn.visitCode();
                Label label0 = new Label();
                Label label1 = new Label();
                Label label2 = new Label();
                mn.visitTryCatchBlock(label0, label1, label2, null);
                Label label3 = new Label();
                mn.visitTryCatchBlock(label2, label3, label2, null);
                Label label4 = new Label();
                mn.visitLabel(label4);
                mn.visitTypeInsn(NEW, "java/util/LinkedList");
                mn.visitInsn(DUP);
                mn.visitMethodInsn(INVOKESPECIAL, "java/util/LinkedList", "<init>", "()V", false);
                mn.visitVarInsn(ASTORE, 2);
                Label label5 = new Label();
                mn.visitLabel(label5);
                mn.visitLdcInsn("");
                mn.visitVarInsn(ALOAD, 1);
                mn.visitVarInsn(ALOAD, 2);
                mn.visitMethodInsn(INVOKESTATIC, "noppes/npcs/util/NBTJsonUtil", "ReadTag", "(Ljava/lang/String;Lnet/minecraft/nbt/NBTBase;Ljava/util/List;)Lnoppes/npcs/util/NBTJsonUtil$JsonLine;", false);
                mn.visitVarInsn(ASTORE, 3);
                Label label6 = new Label();
                mn.visitLabel(label6);
                mn.visitVarInsn(ALOAD, 3);
                mn.visitMethodInsn(INVOKEVIRTUAL, "noppes/npcs/util/NBTJsonUtil$JsonLine", "removeComma", "()V", false);
                Label label7 = new Label();
                mn.visitLabel(label7);
                mn.visitInsn(ACONST_NULL);
                mn.visitVarInsn(ASTORE, 4);
                mn.visitLabel(label0);
                mn.visitTypeInsn(NEW, "java/io/BufferedWriter");
                mn.visitInsn(DUP);
                mn.visitTypeInsn(NEW, "java/io/OutputStreamWriter");
                mn.visitInsn(DUP);
                mn.visitTypeInsn(NEW, "java/io/FileOutputStream");
                mn.visitInsn(DUP);
                mn.visitVarInsn(ALOAD, 0);
                mn.visitMethodInsn(INVOKESPECIAL, "java/io/FileOutputStream", "<init>", "(Ljava/io/File;)V", false);
                mn.visitFieldInsn(GETSTATIC, "java/nio/charset/StandardCharsets", "UTF_8", "Ljava/nio/charset/Charset;");
                mn.visitMethodInsn(INVOKESPECIAL, "java/io/OutputStreamWriter", "<init>", "(Ljava/io/OutputStream;Ljava/nio/charset/Charset;)V", false);
                mn.visitMethodInsn(INVOKESPECIAL, "java/io/BufferedWriter", "<init>", "(Ljava/io/Writer;)V", false);
                mn.visitVarInsn(ASTORE, 4);
                //mn.visitVarInsn(ALOAD, 4);
                //mn.visitLdcInsn("");
                //mn.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write", "(Ljava/lang/String;)V", false);
                Label label8 = new Label();
                mn.visitLabel(label8);
                mn.visitInsn(ICONST_0);
                mn.visitVarInsn(ISTORE, 5);
                Label label9 = new Label();
                mn.visitLabel(label9);
                mn.visitVarInsn(ALOAD, 2);
                mn.visitMethodInsn(INVOKEINTERFACE, "java/util/List", "iterator", "()Ljava/util/Iterator;", true);
                mn.visitVarInsn(ASTORE, 6);
                Label label10 = new Label();
                mn.visitLabel(label10);
                mn.visitFrame(F_FULL, 7, new Object[]{"java/io/File", "net/minecraft/nbt/NBTTagCompound", "java/util/List", "noppes/npcs/util/NBTJsonUtil$JsonLine", "java/io/Writer", INTEGER, "java/util/Iterator"}, 0, new Object[]{});
                mn.visitVarInsn(ALOAD, 6);
                mn.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "hasNext", "()Z", true);
                mn.visitJumpInsn(IFEQ, label1);
                mn.visitVarInsn(ALOAD, 6);
                mn.visitMethodInsn(INVOKEINTERFACE, "java/util/Iterator", "next", "()Ljava/lang/Object;", true);
                mn.visitTypeInsn(CHECKCAST, "noppes/npcs/util/NBTJsonUtil$JsonLine");
                mn.visitVarInsn(ASTORE, 7);
                Label label11 = new Label();
                mn.visitLabel(label11);
                mn.visitVarInsn(ALOAD, 7);
                mn.visitMethodInsn(INVOKEVIRTUAL, "noppes/npcs/util/NBTJsonUtil$JsonLine", "reduceTab", "()Z", false);
                Label label12 = new Label();
                mn.visitJumpInsn(IFEQ, label12);
                mn.visitIincInsn(5, -1);
                mn.visitLabel(label12);
                mn.visitFrame(F_APPEND, 1, new Object[]{"noppes/npcs/util/NBTJsonUtil$JsonLine"}, 0, null);
                mn.visitInsn(ICONST_0);
                mn.visitVarInsn(ISTORE, 8);
                Label label13 = new Label();
                mn.visitLabel(label13);
                mn.visitFrame(F_APPEND, 1, new Object[]{INTEGER}, 0, null);
                mn.visitVarInsn(ILOAD, 8);
                mn.visitVarInsn(ILOAD, 5);
                Label label14 = new Label();
                mn.visitJumpInsn(IF_ICMPGE, label14);
                mn.visitVarInsn(ALOAD, 4);
                mn.visitLdcInsn("    ");
                mn.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write", "(Ljava/lang/String;)V", false);
                mn.visitIincInsn(8, 1);
                mn.visitJumpInsn(GOTO, label13);
                mn.visitLabel(label14);
                mn.visitFrame(F_CHOP, 1, null, 0, null);
                mn.visitVarInsn(ALOAD, 4);
                mn.visitVarInsn(ALOAD, 7);
                mn.visitMethodInsn(INVOKEVIRTUAL, "noppes/npcs/util/NBTJsonUtil$JsonLine", "toString", "()Ljava/lang/String;", false);
                mn.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write", "(Ljava/lang/String;)V", false);
                Label label15 = new Label();
                mn.visitLabel(label15);
                mn.visitVarInsn(ALOAD, 4);
                mn.visitIntInsn(BIPUSH, 10);
                mn.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "write", "(I)V", false);
                Label label16 = new Label();
                mn.visitLabel(label16);
                mn.visitVarInsn(ALOAD, 7);
                mn.visitMethodInsn(INVOKEVIRTUAL, "noppes/npcs/util/NBTJsonUtil$JsonLine", "increaseTab", "()Z", false);
                Label label17 = new Label();
                mn.visitJumpInsn(IFEQ, label17);
                mn.visitIincInsn(5, 1);
                mn.visitLabel(label17);
                mn.visitFrame(F_CHOP, 1, null, 0, null);
                mn.visitJumpInsn(GOTO, label10);
                mn.visitLabel(label1);
                mn.visitFrame(F_CHOP, 2, null, 0, null);
                mn.visitVarInsn(ALOAD, 4);
                Label label18 = new Label();
                mn.visitJumpInsn(IFNULL, label18);
                Label label19 = new Label();
                mn.visitLabel(label19);
                mn.visitVarInsn(ALOAD, 4);
                mn.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "flush", "()V", false);
                Label label20 = new Label();
                mn.visitLabel(label20);
                mn.visitVarInsn(ALOAD, 4);
                mn.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "close", "()V", false);
                mn.visitJumpInsn(GOTO, label18);
                mn.visitLabel(label2);
                mn.visitFrame(F_SAME1, 0, null, 1, new Object[]{"java/lang/Throwable"});
                mn.visitVarInsn(ASTORE, 9);
                mn.visitLabel(label3);
                mn.visitVarInsn(ALOAD, 4);
                Label label21 = new Label();
                mn.visitJumpInsn(IFNULL, label21);
                Label label22 = new Label();
                mn.visitLabel(label22);
                mn.visitVarInsn(ALOAD, 4);
                mn.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "flush", "()V", false);
                Label label23 = new Label();
                mn.visitLabel(label23);
                mn.visitVarInsn(ALOAD, 4);
                mn.visitMethodInsn(INVOKEVIRTUAL, "java/io/Writer", "close", "()V", false);
                mn.visitLabel(label21);
                mn.visitFrame(F_FULL, 10, new Object[]{"java/io/File", "net/minecraft/nbt/NBTTagCompound", "java/util/List", "noppes/npcs/util/NBTJsonUtil$JsonLine", "java/io/Writer", TOP, TOP, TOP, TOP, "java/lang/Throwable"}, 0, new Object[]{});
                mn.visitVarInsn(ALOAD, 9);
                mn.visitInsn(ATHROW);
                mn.visitLabel(label18);
                mn.visitFrame(F_FULL, 5, new Object[]{"java/io/File", "net/minecraft/nbt/NBTTagCompound", "java/util/List", "noppes/npcs/util/NBTJsonUtil$JsonLine", "java/io/Writer"}, 0, new Object[]{});
                mn.visitInsn(RETURN);
                Label label24 = new Label();
                mn.visitLabel(label24);
                mn.visitLocalVariable("i", "I", null, label13, label14, 8);
                mn.visitLocalVariable("tag", "Lnoppes/npcs/util/NBTJsonUtil$JsonLine;", null, label11, label17, 7);
                mn.visitLocalVariable("tab", "I", null, label9, label1, 5);
                mn.visitLocalVariable("file", "Ljava/io/File;", null, label4, label24, 0);
                mn.visitLocalVariable("compound", "Lnet/minecraft/nbt/NBTTagCompound;", null, label4, label24, 1);
                mn.visitLocalVariable("list", "Ljava/util/List;", "Ljava/util/List<Lnoppes/npcs/util/NBTJsonUtil$JsonLine;>;", label5, label24, 2);
                mn.visitLocalVariable("line", "Lnoppes/npcs/util/NBTJsonUtil$JsonLine;", null, label6, label24, 3);
                mn.visitLocalVariable("w", "Ljava/io/Writer;", null, label0, label24, 4);
                mn.visitMaxs(7, 10);
                mn.visitEnd();
                 */
                {
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

    public static JsonLine ReadTag(String name, NBTBase base, List<JsonLine> list) {
        return null;
    }

    public static void SaveFile(File file, NBTTagCompound compound) throws IOException, NBTJsonUtil.JsonException {
        List<JsonLine> list = new LinkedList<>();
        JsonLine line = ReadTag("", compound, list);
        line.removeComma();

        Writer w = null;
        try {
            w = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file), StandardCharsets.UTF_8));
            w.write("");
            int tab = 0;
            for (JsonLine tag : list) {
                if (tag.reduceTab()) --tab;
                for (int i = 0; i < tab; i++) w.write("    ");
                w.write(tag.toString());
                w.write('\n');
                if (tag.increaseTab()) ++tab;
            }
        } finally {
            if (w != null) {
                w.flush();
                w.close();
            }
        }

    }

    static class JsonLine {
        private String line;

        public JsonLine(String line) {
            this.line = line;
        }

        public void removeComma() {
            if (this.line.endsWith(",")) {
                this.line = this.line.substring(0, this.line.length() - 1);
            }

        }

        public boolean reduceTab() {
            int length = this.line.length();
            return length == 1 && (this.line.endsWith("}") || this.line.endsWith("]")) || length == 2 && (this.line.endsWith("},") || this.line.endsWith("],"));
        }

        public boolean increaseTab() {
            return this.line.endsWith("{") || this.line.endsWith("[");
        }

        public String toString() {
            return this.line;
        }
    }

}
