package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class BlockBuilderClassVisitor extends BlockNpcRedstoneClassVisitor {
    public BlockBuilderClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    protected MethodVisitor activatedVisitor(int api, MethodVisitor mv) {
        return new RevertWorldRemoteVisitor(api, mv);
    }

    private static class RevertWorldRemoteVisitor extends MethodVisitor {

        public RevertWorldRemoteVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        boolean encounter = false;
        boolean dealt = false;
        Label label1;

        @Override
        public void visitFieldInsn(int opcode, String owner, String name, String desc) {
            super.visitFieldInsn(opcode, owner, name, desc);
            if (!encounter && opcode == GETFIELD && desc.equals("Z")) encounter = true;
        }

        @Override
        public void visitJumpInsn(int opcode, Label label) {
            if (!dealt && encounter) {
                dealt = true;
                label1 = new Label();
                super.visitJumpInsn(IFNE, label1);

                super.visitLabel(new Label());
                super.visitVarInsn(ALOAD, 4);
                super.visitTypeInsn(CHECKCAST, "net/minecraft/entity/player/EntityPlayer");
                super.visitFieldInsn(GETSTATIC, "noppes/npcs/CustomNpcsPermissions", "EDIT_BLOCKS", "Lnoppes/npcs/CustomNpcsPermissions$Permission;");
                super.visitMethodInsn(INVOKESTATIC, "noppes/npcs/CustomNpcsPermissions", "hasPermission", "(Lnet/minecraft/entity/player/EntityPlayer;Lnoppes/npcs/CustomNpcsPermissions$Permission;)Z", false);
                super.visitJumpInsn(IFNE, label);

                super.visitLabel(label1);
                super.visitFrame(F_SAME, 0, null, 0, null);

            } else super.visitJumpInsn(opcode, label);
        }
    }
}
