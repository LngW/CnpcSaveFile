package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ClientEventHandlerClassVisitor extends ClassVisitor {
    public ClientEventHandlerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visitEnd() {
        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "onClientDisconnected", "(Lnet/minecraftforge/fml/common/network/FMLNetworkEvent$ClientDisconnectionFromServerEvent;)V", null, null);
        mv.visitAnnotation("Lnet/minecraftforge/fml/common/eventhandler/SubscribeEvent;", true).visitEnd();
        mv.visitCode();
        Label label0 = new Label();
        Label label1 = new Label();
        Label label2 = new Label();
        mv.visitLabel(label0);
        mv.visitFieldInsn(Opcodes.GETSTATIC, "noppes/npcs/client/ClientProxy", "playerData", "Lnoppes/npcs/controllers/data/PlayerData;");
        mv.visitInsn(Opcodes.ACONST_NULL);
        mv.visitFieldInsn(Opcodes.PUTFIELD, "noppes/npcs/controllers/data/PlayerData", "player", "Lnet/minecraft/entity/player/EntityPlayer;");
        mv.visitInsn(Opcodes.RETURN);
        mv.visitLabel(label1);
        mv.visitLocalVariable("this", "Lnoppes/npcs/client/ClientEventHandler;", null, label0, label1, 0);
        mv.visitLocalVariable("event", "Lnet/minecraftforge/fml/common/network/FMLNetworkEvent$ClientDisconnectionFromServerEvent;", null, label0, label1, 1);
        mv.visitMaxs(2, 2);
        mv.visitEnd();
        super.visitEnd();
    }
}
