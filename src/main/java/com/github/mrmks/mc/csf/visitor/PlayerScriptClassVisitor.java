package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;
import static org.objectweb.asm.Opcodes.INVOKEVIRTUAL;

public class PlayerScriptClassVisitor extends ClassVisitor {
    public PlayerScriptClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    private boolean f = false;
    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!f && "noticeString".equals(name)) {
            f = true;
            return new MethodVisitorImpl(super.visitMethod(access, name, desc, signature, exceptions));
        } else return super.visitMethod(access, name, desc, signature, exceptions);
    }


    private static class MethodVisitorImpl extends MethodVisitor {
        private boolean f = false;
        public MethodVisitorImpl(MethodVisitor mv) {
            super(ASM5, mv);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (!f && "com/google/common/base/MoreObjects$ToStringHelper".equals(owner) && "toString".equals(name)) {
                f = true;
                boolean d = FMLLaunchHandler.isDeobfuscatedEnvironment();
                mv.visitLdcInsn("player");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "noppes/npcs/controllers/data/PlayerScriptData", "player", "Lnet/minecraft/entity/player/EntityPlayer;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/entity/player/EntityPlayer", d ? "getGameProfile" : "func_146103_bH", "()Lcom/mojang/authlib/GameProfile;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/mojang/authlib/GameProfile", "getName", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/google/common/base/MoreObjects$ToStringHelper", "add", "(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/common/base/MoreObjects$ToStringHelper;", false);
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }


}
