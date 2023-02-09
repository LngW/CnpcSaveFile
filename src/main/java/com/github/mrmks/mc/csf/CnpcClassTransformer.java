package com.github.mrmks.mc.csf;

import com.github.mrmks.mc.csf.visitor.*;
import net.minecraft.launchwrapper.IClassTransformer;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
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
            "noppes.npcs.constants.EnumPacketServer",
            "noppes.npcs.CustomNpcsPermissions",
            "noppes.npcs.items.ItemNbtBook",
            "noppes.npcs.items.ItemScriptedDoor",
            "noppes.npcs.items.ItemNpcCloner",
            "noppes.npcs.blocks.BlockNpcRedstone",
            "noppes.npcs.blocks.BlockWaypoint",
            "noppes.npcs.blocks.BlockBorder",
            "noppes.npcs.blocks.BlockBuilder",
            "noppes.npcs.blocks.BlockCopy",
            "noppes.npcs.blocks.BlockScriptedDoor",
            "noppes.npcs.blocks.BlockScripted",
            "noppes.npcs.roles.JobChunkLoader",
            "noppes.npcs.entity.EntityNPCInterface",
            "noppes.npcs.entity.data.DataDisplay",
            "noppes.npcs.controllers.ScriptContainer",
            "noppes.npcs.Server"
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
            EnumPacketServerClassVisitor::new,
            CustomNpcsPermissionsClassVisitor::new,
            ItemNbtBookClassVisitor::new,
            ItemScriptedDoorClassVisitor::new,
            ItemClonerClassVisitor::new,
            BlockNpcRedstoneClassVisitor::new,
            BlockNpcRedstoneClassVisitor::new,
            BlockBorderClassVisitor::new,
            BlockBuilderClassVisitor::new,
            BlockBuilderClassVisitor::new,
            BlockScriptedDoorClassVisitor::new,
            BlockScriptedClassVisitor::new,
            JobChunkLoaderClassVisitor::new,
            EntityNPCInterfaceClassVisitor::new,
            DataDisplayClassVisitor::new,
            ScriptContainerClassVisitor::new,
            ServerClassVisitor::new
    };
    private final String[] nameListClient = new String[] {
            "noppes.npcs.client.layer.LayerInterface",
            "noppes.npcs.client.ClientEventHandler",
    };
    private final TransformerBuilder[] transListClient = new TransformerBuilder[] {
            LayerInterfaceClassVisitor::new,
            ClientEventHandlerClassVisitor::new,
    };
    private final String[] nameListServer = new String[]{
    };
    private final TransformerBuilder[] transListServer = new TransformerBuilder[]{
    };

    private final boolean isClient = FMLLaunchHandler.side().isClient();

    @Override
    public byte[] transform(String name, String transformedName, byte[] basicClass) {

        // since the LaunchClassLoader go through all transformers, it should be ok we compare our targets here.
        // besides, to be compatible with SpongePowered Mixin, we should make sure that
        // the class have the same structure after it goes through this transformer multiple times.
        int i;
        if ((i = matchClass(name, nameList)) >= 0)
            return runVisitor(transList[i], name, basicClass);

        String[] names = isClient ? nameListClient : nameListServer;
        TransformerBuilder[] builders = isClient ? transListClient : transListServer;

        if ((i = matchClass(name, names)) >= 0)
            return runVisitor(builders[i], name, basicClass);

        return basicClass;
    }

    private static int matchClass(String name, String[] names) {
        for (int i = 0; i < names.length; i ++) {
            if (names[i].equals(name)) return i;
        }
        return -1;
    }

    private static byte[] runVisitor(TransformerBuilder builder, String name, byte[] basic) {
        ClassReader cr = new ClassReader(basic);
        ClassWriter cw = new ClassWriter(cr, 0);
        ClassVisitor cv = builder.build(ASM5, cw);
        cr.accept(cv, 0);

        return TransformHelper.transformedSave(name, cw.toByteArray());
    }

    private interface TransformerBuilder {
        ClassVisitor build(int api, ClassVisitor cv);
    }
}
