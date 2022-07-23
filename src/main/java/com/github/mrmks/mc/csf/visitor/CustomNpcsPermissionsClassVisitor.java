package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.FieldVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class CustomNpcsPermissionsClassVisitor extends ClassVisitor {
    public CustomNpcsPermissionsClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
        if (access == 0x19 && name.equals("TOOL_NBTBOOK")) {
            super.visitField(access, "TOOL_TELEPORTER", desc, signature, value).visitEnd();
        }
        return super.visitField(access, name, desc, signature, value);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<clinit>".equals(name)) {
            return new MethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitCode() {
                    super.visitCode();
                    super.visitTypeInsn(Opcodes.NEW, "noppes/npcs/CustomNpcsPermissions$Permission");
                    super.visitInsn(Opcodes.DUP);
                    super.visitLdcInsn("customnpcs.tool.teleporter");
                    super.visitMethodInsn(Opcodes.INVOKESPECIAL, "noppes/npcs/CustomNpcsPermissions$Permission", "<init>", "(Ljava/lang/String;)V", false);
                    super.visitFieldInsn(Opcodes.PUTSTATIC, "noppes/npcs/CustomNpcsPermissions", "TOOL_TELEPORTER", "Lnoppes/npcs/CustomNpcsPermissions$Permission;");
                }
            };
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }
}
