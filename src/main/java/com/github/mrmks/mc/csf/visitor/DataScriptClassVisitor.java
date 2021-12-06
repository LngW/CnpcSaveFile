package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class DataScriptClassVisitor extends ClassVisitor {
    public DataScriptClassVisitor(ClassVisitor cv) {
        super(ASM5, cv);
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
                mv.visitLdcInsn("world");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "noppes/npcs/entity/data/DataScript", "npc", "Lnoppes/npcs/entity/EntityNPCInterface;");
                mv.visitFieldInsn(GETFIELD, "noppes/npcs/entity/EntityNPCInterface", d ? "world" : "field_70170_p", "Lnet/minecraft/world/World;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/World", d ? "getWorldInfo" : "func_72912_H", "()Lnet/minecraft/world/storage/WorldInfo;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/storage/WorldInfo", d ? "getWorldName" : "func_76065_j", "()Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/google/common/base/MoreObjects$ToStringHelper", "add", "(Ljava/lang/String;Ljava/lang/Object;)Lcom/google/common/base/MoreObjects$ToStringHelper;", false);
                mv.visitLdcInsn("dim");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "noppes/npcs/entity/data/DataScript", "npc", "Lnoppes/npcs/entity/EntityNPCInterface;");
                mv.visitFieldInsn(GETFIELD, "noppes/npcs/entity/EntityNPCInterface", d ? "world" : "field_70170_p", "Lnet/minecraft/world/World;");
                mv.visitFieldInsn(GETFIELD, "net/minecraft/world/World", d ? "provider" : "field_73011_w", "Lnet/minecraft/world/WorldProvider;");
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/world/WorldProvider", "getDimension", "()I", false);
                mv.visitMethodInsn(INVOKEVIRTUAL, "com/google/common/base/MoreObjects$ToStringHelper", "add", "(Ljava/lang/String;I)Lcom/google/common/base/MoreObjects$ToStringHelper;", false);
            }
            super.visitMethodInsn(opcode, owner, name, desc, itf);
        }
    }

}
