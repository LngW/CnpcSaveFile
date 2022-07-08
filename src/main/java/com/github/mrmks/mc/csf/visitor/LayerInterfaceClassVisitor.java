package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class LayerInterfaceClassVisitor extends ClassVisitor {
    public LayerInterfaceClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    boolean flag = false;

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!flag && (FMLLaunchHandler.isDeobfuscatedEnvironment() ? "doRenderLayer" : "func_177141_a").equals(name)) {
            flag = true;
            return new MethodVisitor(api,super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitInsn(int code) {
                    if (code == Opcodes.RETURN) {
                        mv.visitVarInsn(Opcodes.ALOAD, 0);
                        mv.visitInsn(Opcodes.ACONST_NULL);
                        mv.visitFieldInsn(Opcodes.PUTFIELD, "noppes/npcs/client/layer/LayerInterface", "npc", "Lnoppes/npcs/entity/EntityCustomNpc;");
                    }
                    super.visitInsn(code);
                }
            };
        } else return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
