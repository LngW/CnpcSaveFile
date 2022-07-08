package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.*;

import static org.objectweb.asm.Opcodes.*;

public class CustomNpcsClassVisitor extends ClassVisitor {
    public CustomNpcsClassVisitor(int i, ClassVisitor classVisitor) {
        super(i, classVisitor);
    }

    boolean f_stopped = false;

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!f_stopped && "stopped".equalsIgnoreCase(name)) {
            f_stopped = true;
            return new MethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitCode() {
                    super.visitMethodInsn(INVOKESTATIC, "noppes/npcs/api/wrapper/WrapperNpcAPI", "clearCache", "()V", false);
                    super.visitCode();
                }
            };
        } else return super.visitMethod(access, name, desc, signature, exceptions);
    }

    @Override
    public void visitEnd() {
        MethodVisitor methodVisitor;
        methodVisitor = visitMethod(ACC_PUBLIC, "serverStopping", "(Lnet/minecraftforge/fml/common/event/FMLServerStoppingEvent;)V", null, null);
        {
            AnnotationVisitor annotationVisitor0;
            annotationVisitor0 = methodVisitor.visitAnnotation("Lnet/minecraftforge/fml/common/Mod$EventHandler;", true);
            annotationVisitor0.visitEnd();
        }
        methodVisitor.visitCode();
        Label label0 = new Label();
        methodVisitor.visitLabel(label0);
        String getAdv = FMLLaunchHandler.isDeobfuscatedEnvironment() ? "getAdvancements" : "func_192039_O";
        String disp = FMLLaunchHandler.isDeobfuscatedEnvironment() ? "dispose" : "func_192745_a";
        methodVisitor.visitFieldInsn(GETSTATIC, "noppes/npcs/entity/EntityNPCInterface", "ChatEventPlayer", "Lnet/minecraftforge/common/util/FakePlayer;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraftforge/common/util/FakePlayer", getAdv, "()Lnet/minecraft/advancements/PlayerAdvancements;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/advancements/PlayerAdvancements", disp, "()V", false);
        methodVisitor.visitFieldInsn(GETSTATIC, "noppes/npcs/entity/EntityNPCInterface", "CommandPlayer", "Lnet/minecraftforge/common/util/FakePlayer;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraftforge/common/util/FakePlayer", getAdv, "()Lnet/minecraft/advancements/PlayerAdvancements;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/advancements/PlayerAdvancements", disp, "()V", false);
        methodVisitor.visitFieldInsn(GETSTATIC, "noppes/npcs/entity/EntityNPCInterface", "GenericPlayer", "Lnet/minecraftforge/common/util/FakePlayer;");
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraftforge/common/util/FakePlayer", getAdv, "()Lnet/minecraft/advancements/PlayerAdvancements;", false);
        methodVisitor.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/advancements/PlayerAdvancements", disp, "()V", false);
        Label label1 = new Label();
        methodVisitor.visitLabel(label1);
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitFieldInsn(PUTSTATIC, "noppes/npcs/entity/EntityNPCInterface", "ChatEventPlayer", "Lnet/minecraftforge/common/util/FakePlayer;");
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitFieldInsn(PUTSTATIC, "noppes/npcs/entity/EntityNPCInterface", "CommandPlayer", "Lnet/minecraftforge/common/util/FakePlayer;");
        methodVisitor.visitInsn(ACONST_NULL);
        methodVisitor.visitFieldInsn(PUTSTATIC, "noppes/npcs/entity/EntityNPCInterface", "GenericPlayer", "Lnet/minecraftforge/common/util/FakePlayer;");
        methodVisitor.visitInsn(RETURN);
        Label label2 = new Label();
        methodVisitor.visitLabel(label2);
        methodVisitor.visitLocalVariable("this", "Lcom/github/mrmks/mc/cscriptjava/CnpcScriptJava;", null, label0, label2, 0);
        methodVisitor.visitLocalVariable("e", "Lnet/minecraftforge/fml/common/event/FMLServerStoppingEvent;", null, label0, label2, 1);
        methodVisitor.visitMaxs(2, 2);
        methodVisitor.visitEnd();
        super.visitEnd();
    }
}
