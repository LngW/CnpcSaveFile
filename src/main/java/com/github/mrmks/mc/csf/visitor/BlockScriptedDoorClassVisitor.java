package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;
import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import static org.objectweb.asm.Opcodes.*;

public class BlockScriptedDoorClassVisitor extends ClassVisitor {
    public BlockScriptedDoorClassVisitor(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onBlockActivated" : "func_180639_a")) {
            return new MethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions)) {
                boolean encounter = false;
                boolean dealt = false;

                @Override
                public void visitVarInsn(int opcode, int var) {
                    super.visitVarInsn(opcode, var);
                    if (!encounter && opcode == ALOAD && var == 12) encounter = true;
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
        } else return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
