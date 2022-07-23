package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class ItemNbtBookClassVisitor extends ClassVisitor {
    public ItemNbtBookClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("blockEvent".equals(name)) {
            return new BlockEventVisitor(api, mv, "RightClickBlock");
        } else if ("entityEvent".equals(name)) {
            return new BlockEventVisitor(api, mv, "EntityInteract");
        } else {
            return mv;
        }
    }

    private static class BlockEventVisitor extends MethodVisitor {

        String name;
        boolean first = true;


        public BlockEventVisitor(int api, MethodVisitor mv, String name) {
            super(api, mv);
            this.name = name;
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            super.visitMethodInsn(opcode, owner, name, desc, itf);
            if (first && opcode == INVOKEVIRTUAL && owner.startsWith("net/minecraftforge/event/entity/player/PlayerInteractEvent$")) {
                first = false;

                super.visitFieldInsn(GETSTATIC, "noppes/npcs/CustomNpcsPermissions", "TOOL_NBTBOOK", "Lnoppes/npcs/CustomNpcsPermissions$Permission;");
                super.visitMethodInsn(INVOKESTATIC, "noppes/npcs/CustomNpcsPermissions", "hasPermission", "(Lnet/minecraft/entity/player/EntityPlayer;Lnoppes/npcs/CustomNpcsPermissions$Permission;)Z", false);
                Label label1 = new Label();
                super.visitJumpInsn(IFNE, label1);
                super.visitInsn(RETURN);
                super.visitLabel(label1);
                super.visitFrame(F_SAME, 0, null, 0, null);

                super.visitVarInsn(ALOAD, 1);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }
}
