package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class ItemClonerClassVisitor extends ClassVisitor {
    public ItemClonerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (name.equals(FMLLaunchHandler.isDeobfuscatedEnvironment() ? "onItemUse" : "func_180614_a")) {
            return new MethodVisitor(api, mv) {
                boolean encounter = false;
                boolean dealt = false;

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
                        super.visitVarInsn(ALOAD, 1);
                        super.visitFieldInsn(GETSTATIC, "noppes/npcs/CustomNpcsPermissions", "NPC_CLONE", "Lnoppes/npcs/CustomNpcsPermissions$Permission;");
                        super.visitMethodInsn(INVOKESTATIC, "noppes/npcs/CustomNpcsPermissions", "hasPermission", "(Lnet/minecraft/entity/player/EntityPlayer;Lnoppes/npcs/CustomNpcsPermissions$Permission;)Z", false);
                        super.visitJumpInsn(IFEQ, label);
                    }
                }
            };
        } else return mv;
    }
}
