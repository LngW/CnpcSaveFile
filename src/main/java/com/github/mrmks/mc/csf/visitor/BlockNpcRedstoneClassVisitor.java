package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class BlockNpcRedstoneClassVisitor extends ClassVisitor {
    public BlockNpcRedstoneClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onBlockPlacedBy" : "func_180633_a")) {
            return new WorldIsRemoteVisitor(api, mv, true);
        } else if (name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onBlockActivated" : "func_180639_a")) {
            return activatedVisitor(api, mv);
        } else return mv;
    }

    protected MethodVisitor activatedVisitor(int api, MethodVisitor mv) {
        return mv;
    }

    protected static class WorldIsRemoteVisitor extends MethodVisitor {

        public WorldIsRemoteVisitor(int api, MethodVisitor mv, boolean doCheck) {
            super(api, mv);
            this.check = doCheck;
        }

        boolean encounter = false;
        boolean dealt = false;
        boolean check;

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, name, desc);
            if (!encounter && opcode == GETFIELD && desc.equals("Z")) encounter = true;
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            super.visitJumpInsn(opcode, label);
            if (!dealt && encounter) {
                dealt = true;

                super.visitLabel(new Label());
                super.visitVarInsn(ALOAD, 4);
                if (check) super.visitTypeInsn(CHECKCAST, "net/minecraft/entity/player/EntityPlayer");
                super.visitFieldInsn(GETSTATIC, "noppes/npcs/CustomNpcsPermissions", "EDIT_BLOCKS", "Lnoppes/npcs/CustomNpcsPermissions$Permission;");
                super.visitMethodInsn(INVOKESTATIC, "noppes/npcs/CustomNpcsPermissions", "hasPermission", "(Lnet/minecraft/entity/player/EntityPlayer;Lnoppes/npcs/CustomNpcsPermissions$Permission;)Z", false);
                super.visitJumpInsn(IFEQ, label);
            }
        }
    }
}
