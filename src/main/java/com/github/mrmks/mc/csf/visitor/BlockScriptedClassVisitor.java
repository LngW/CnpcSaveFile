package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import static org.objectweb.asm.Opcodes.*;

public class BlockScriptedClassVisitor extends BlockNpcRedstoneClassVisitor {
    public BlockScriptedClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    protected MethodVisitor activatedVisitor(int api, MethodVisitor mv) {
        return new MethodVisitor(api, mv) {
            boolean encounter = false;
            boolean dealt = false;

            @Override
            public void visitVarInsn(int opcode, int var) {
                super.visitVarInsn(opcode, var);
                if (!encounter && opcode == ALOAD && var == 10)
                    encounter = true;
            }

            @Override
            public void visitJumpInsn(int opcode, Label label) {
                super.visitJumpInsn(opcode, label);
                if (encounter && !dealt) {
                    dealt = true;

                    super.visitLabel(new Label());
                    super.visitVarInsn(ALOAD, 4);
                    super.visitFieldInsn(GETSTATIC, "noppes/npcs/CustomNpcsPermissions", "TOOL_SCRIPTER", "Lnoppes/npcs/CustomNpcsPermissions$Permission;");
                    super.visitMethodInsn(INVOKESTATIC, "noppes/npcs/CustomNpcsPermissions", "hasPermission", "(Lnet/minecraft/entity/player/EntityPlayer;Lnoppes/npcs/CustomNpcsPermissions$Permission;)Z", false);
                    super.visitJumpInsn(IFEQ, label);
                }
            }
        };
    }
}
