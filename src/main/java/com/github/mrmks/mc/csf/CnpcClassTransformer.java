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

    private final boolean isClient = FMLLaunchHandler.side().isClient();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        // since the LaunchClassLoader go through all transformers, it should be ok if we compare our targets.
        // besides, to be compatible with SpongePowered Mixin, we should make sure that
        // the class have the same structure after it goes through this transformer multiple times.
        for (int i = 0; i < nameList.length; i++) {
            if (nameList[i].equals(name)) {

                ClassReader cr = new ClassReader(basicClass);
                ClassWriter cw = new ClassWriter(cr ,0);
                ClassVisitor cv = transList[i].build(ASM5, cw);
                cr.accept(cv, 0);

                return TransformHelper.transformedSave(name, cw.toByteArray());
            }
        }

        String[] names = isClient ? nameListClient : nameListServer;
        TransformerBuilder[] builders = isClient ? transListClient : transListServer;
        for (int i = 0; i < names.length; i++) {
            if (names[i].equals(name)) {

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
