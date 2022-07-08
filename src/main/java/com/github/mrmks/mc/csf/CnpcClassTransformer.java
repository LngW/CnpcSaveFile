package com.github.mrmks.mc.csf;

import com.github.mrmks.mc.csf.visitor.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import net.minecraftforge.fml.relauncher.Side;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.ClassWriter;

import static org.objectweb.asm.Opcodes.ASM5;

public class CnpcClassTransformer implements IClassTransformer {

    private final String[] nameList = new String[]{
            "noppes.npcs.util.NBTJsonUtil",
            "noppes.npcs.util.NBTJsonUtil$JsonException",
            "com.github.mrmks.mc.injector.InjectorJsonUtil",
            "noppes.npcs.entity.EntityCustomNpc",
            "noppes.npcs.entity.data.DataScript",
            "noppes.npcs.blocks.tiles.TileScripted",
            "noppes.npcs.blocks.tiles.TileScriptedDoor",
            "noppes.npcs.controllers.data.PlayerScriptData",
            "noppes.npcs.controllers.data.PlayerData",
            "noppes.npcs.CustomNpcs",
    };
    private final TransformerBuilder[] transList = new TransformerBuilder[] {
            NBTJsonUtilClassVisitor::new,
            JsonExceptionClassVisitor::new,
            InjectorJsonUtilClassVisitor::new,
            EntityCustomNpcClassVisitor::new,
            DataScriptClassVisitor::new,
            TileScriptedClassVisitor::new,
            TileScriptedDoorClassVisitor::new,
            PlayerScriptClassVisitor::new,
            PlayerDataClassVisitor::new,
            CustomNpcsClassVisitor::new,
    };
    private final String[] nameListClient = new String[] {
            "noppes.npcs.client.layer.LayerInterface",
    };
    private final TransformerBuilder[] transListClient = new TransformerBuilder[] {
            LayerInterfaceClassVisitor::new,
    };
    private final String[] nameListServer = new String[]{};
    private final TransformerBuilder[] transListServer = new TransformerBuilder[]{};

    private boolean[] passFlag = null;
    private int count = 0;
    private boolean pass = false;

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        if (passFlag == null) {
            int ext = FMLLaunchHandler.side().isClient() ? nameListClient.length : nameListServer.length;
            passFlag = new boolean[ext + nameList.length];
        }

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

        String[] names = FMLLaunchHandler.side().isClient() ? nameListClient : nameListServer;
        TransformerBuilder[] builders = FMLLaunchHandler.side().isClient() ? transListClient : transListServer;
        for (int i = 0; i < names.length; i++) {
            if (!passFlag[i + nameList.length] && names[i].equals(name)) {
                passFlag[i + nameList.length] = true;
                pass = ++count == passFlag.length;

                ClassReader cr = new ClassReader(basicClass);
                ClassWriter cw = new ClassWriter(cr ,0);
                ClassVisitor cv = builders[i].build(ASM5, cw);
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
