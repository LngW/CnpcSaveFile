package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.AnnotationVisitor;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CmdCloneClassVisitor extends ClassVisitor {
    public CmdCloneClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visitEnd() {

        MethodVisitor mv = super.visitMethod(Opcodes.ACC_PUBLIC, "flush", "(Lnet/minecraft/server/MinecraftServer;Lnet/minecraft/command/ICommandSender;[Ljava/lang/String;)V", null, null);
        AnnotationVisitor av = mv.visitAnnotation("Lnoppes/npcs/api/CommandNoppesBase$SubCommand;", true);
        av.visit("desc", "Flush clone caches to nul to reload clones from disk");
        av.visit("usage", "");
        av.visit("permission", 4);
        av.visitEnd();

        mv.visitFieldInsn(Opcodes.GETSTATIC, "noppes/npcs/controllers/ServerCloneController", "Instance", "Lnoppes/npcs/controllers/ServerCloneController;");
        mv.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "noppes/npcs/controllers/ServerCloneController", "flushCaches", "()V", false);
        mv.visitInsn(Opcodes.RETURN);
        mv.visitMaxs(1, 4);
        mv.visitEnd();

        super.visitEnd();
    }
}
