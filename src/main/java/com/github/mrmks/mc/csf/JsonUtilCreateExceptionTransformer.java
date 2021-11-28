package com.github.mrmks.mc.csf;

import net.minecraftforge.fml.common.FMLLog;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Label;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodNode;
import net.minecraft.launchwrapper.IClassTransformer;

import static org.objectweb.asm.Opcodes.*;

public class JsonUtilCreateExceptionTransformer implements IClassTransformer {
    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {
        if (name != null && name.equals(transformedName) && name.equals("com.github.mrmks.mc.json.JsonUtil")) {
            ClassReader cr = new ClassReader(basicClass);
            ClassNode cn = new ClassNode();
            cr.accept(cn, 0);

            cn.visitInnerClass("noppes/npcs/util/NBTJsonUtil$JsonException","noppes/npcs/util/NBTJsonUtil", "JsonException", ACC_PUBLIC | ACC_STATIC);

            for (MethodNode mn : cn.methods) {
                if (mn.name.equals("createException")) {
                    mn.localVariables.clear();
                    mn.exceptions.clear();
                    mn.instructions.clear();

                    mn.visitCode();
                    Label label0 = new Label();
                    mn.visitLabel(label0);
                    mn.visitTypeInsn(NEW, "noppes/npcs/util/NBTJsonUtil$JsonException");
                    mn.visitInsn(DUP);
                    mn.visitVarInsn(ALOAD, 0);
                    mn.visitVarInsn(ALOAD, 1);
                    mn.visitMethodInsn(INVOKESPECIAL, "noppes/npcs/util/NBTJsonUtil$JsonException", "<init>", "(Ljava/lang/String;Lcom/github/mrmks/mc/json/JsonToken;)V", false);
                    mn.visitInsn(ARETURN);
                    Label label1 = new Label();
                    mn.visitLabel(label1);
                    mn.visitLocalVariable("msg", "Ljava/lang/String;", null, label0, label1, 0);
                    mn.visitLocalVariable("token", "Lcom/github/mrmks/mc/json/JsonToken;", null, label0, label1, 1);
                    mn.visitMaxs(4,2);
                    mn.visitEnd();
                    break;
                }
            }
            ClassWriter cw = new ClassWriter(0);
            cn.accept(cw);

            return DumpHelper.saveDump("com.github.mrmks.mc.json.JsonUtil", cw.toByteArray());
        }
        return basicClass;
    }
}
