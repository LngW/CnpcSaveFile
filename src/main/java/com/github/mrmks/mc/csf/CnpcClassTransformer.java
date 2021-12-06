package com.github.mrmks.mc.csf;

import com.github.mrmks.mc.csf.visitor.*;
import net.minecraft.launchwrapper.IClassTransformer;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ASM5;

public class CnpcClassTransformer implements IClassTransformer {

    private final String[] nameList = new String[]{
            "noppes.npcs.util.NBTJsonUtil",
            "noppes.npcs.util.NBTJsonUtil$JsonException",
            "com.github.mrmks.mc.json.JsonUtil",
            "noppes.npcs.entity.EntityCustomNpc",
            "noppes.npcs.entity.data.DataScript"
    };
    private final TransformerBuilder[] transList = new TransformerBuilder[] {
            (api, cv) -> new NBTJsonUtilClassVisitor(cv),
            (api, cv) -> new JsonExceptionClassVisitor(cv),
            (api, cv) -> new JsonUtilClassVisitor(cv),
            (api, cv) -> new EntityCustomNpcClassVisitor(cv),
            (api, cv) -> new DataScriptClassVisitor(cv)
    };
    private final boolean[] passFlag = new boolean[nameList.length];
    private int count = 0;
    private boolean pass = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (pass) return basicClass;

        for (int i = 0; i < nameList.length; i++) {
            if (!passFlag[i] && nameList[i].equals(name)) {
                passFlag[i] = true;
                pass = ++count == passFlag.length;

                ClassReader cr = new ClassReader(basicClass);
                ClassWriter cw = new ClassWriter(cr ,0);
                ClassVisitor cv = transList[i].build(ASM5, cw);
                cr.accept(cv, 0);

                return TransformHelper.transformedSave(name, cw.toByteArray());
            }
        }

        return basicClass;
    }

    private interface TransformerBuilder {
        ClassVisitor build(int api, ClassVisitor cv);
    }
}
