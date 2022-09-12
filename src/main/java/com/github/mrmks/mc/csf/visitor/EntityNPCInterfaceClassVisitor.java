package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class EntityNPCInterfaceClassVisitor extends ClassVisitor {
    public EntityNPCInterfaceClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);

        if (name.equals("reset")) {
            mv = new MethodVisitor(api, mv) {
                @Override
                public void visitCode() {
                    super.visitCode();
                    super.visitVarInsn(Opcodes.ALOAD, 0);
                    super.visitInsn(Opcodes.ICONST_0);
                    super.visitFieldInsn(Opcodes.PUTFIELD, "noppes/npcs/entity/EntityNPCInterface", FMLLaunchHandler.isDeobfuscatedEnvironment() ? "dead" : "field_70729_aU", "Z");
                }
            };
        }

        return mv;
    }
}
