package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

public class ScriptContainerClassVisitor extends ClassVisitor {
    public ScriptContainerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if ("run".equals(name) && "(Ljava/lang/String;Ljava/lang/Object;)V".equals(desc)) {
            mv = new MethodVisitor(api, mv) {
                boolean flag = false;
                @Override
                public void visitTypeInsn(int opcode, String type) {
                    if (opcode == Opcodes.NEW && "java/io/PrintWriter".equals(type)) {
                        flag = true;
                    } else super.visitTypeInsn(opcode, type);
                }

                @Override
                public void visitInsn(int opcode) {
                    if (flag && opcode == Opcodes.DUP) return;
                    super.visitInsn(opcode);
                }

                @Override
                public void visitVarInsn(int opcode, int var) {
                    if (flag) {
                        if (opcode == Opcodes.ALOAD && var == 4) return;
                        else if (opcode == Opcodes.ASTORE && var == 5) {
                            flag = false;
                            super.visitInsn(Opcodes.ACONST_NULL);
                        }
                    }
                    super.visitVarInsn(opcode, var);
                }

                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (flag) {
                        return;
                    }
                    if ("java/lang/Throwable".equals(owner) && "printStackTrace".equals(name) && "(Ljava/io/PrintWriter;)V".equals(desc)) {
                        super.visitInsn(Opcodes.POP);
                        super.visitTypeInsn(Opcodes.NEW, "java/io/PrintWriter");
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ALOAD, 4);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/io/PrintWriter", "<init>", "(Ljava/io/Writer;)V", false);
                        super.visitInsn(Opcodes.DUP);
                        super.visitVarInsn(Opcodes.ASTORE, 5);
                    } else if ("javax/script/ScriptContext".equals(owner) && "(Ljava/io/Writer;)V".equals(desc)) {
                        if ("setWriter".equals(name) || "setErrorWriter".equals(name)) {
                            super.visitInsn(Opcodes.POP);
                            super.visitVarInsn(Opcodes.ALOAD, 4);
                        }
                    } else if ("java/io/PrintWriter".equals(owner) && "close".equals(name)) {
                        super.visitInsn(Opcodes.POP);
                        super.visitVarInsn(Opcodes.ALOAD, 4);
                        super.visitMethodInsn(opcode, "java/io/StringWriter", name, desc, itf);
                        return;
                    }

                    super.visitMethodInsn(opcode, owner, name, desc, itf);

                    if ("noppes/npcs/NoppesUtilServer".equals(owner) && "NotifyOPs".equals(name)) {
                        super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "noppes/npcs/controllers/ScriptContainer", "handler", "Lnoppes/npcs/controllers/IScriptHandler;");
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "noppes/npcs/controllers/IScriptHandler", "noticeString", "()Ljava/lang/String;", true);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitLdcInsn(" script errored");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                        super.visitVarInsn(Opcodes.ALOAD, 6);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "noppes/npcs/LogWriter", "throwing", "(Ljava/lang/Object;Ljava/lang/Throwable;)V", false);

                        // engine name
                        super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                        super.visitLdcInsn("Engine name is: ");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "noppes/npcs/controllers/ScriptContainer", "currentScriptLanguage", "Ljava/lang/String;");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "noppes/npcs/LogWriter", "error", "(Ljava/lang/Object;)V", false);

                        // full code
                        super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                        super.visitLdcInsn("Full code is as below: \n");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "noppes/npcs/controllers/ScriptContainer", "getFullCode", "()Ljava/lang/String;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "noppes/npcs/LogWriter", "error", "(Ljava/lang/Object;)V", false);
                    } else if ("javax/script/ScriptEngine".equals(owner) && "eval".equals(name)) {
                        super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "noppes/npcs/controllers/ScriptContainer", "handler", "Lnoppes/npcs/controllers/IScriptHandler;");
                        super.visitMethodInsn(Opcodes.INVOKEINTERFACE, "noppes/npcs/controllers/IScriptHandler", "noticeString", "()Ljava/lang/String;", true);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitLdcInsn(" script initialized");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "noppes/npcs/LogWriter", "info", "(Ljava/lang/Object;)V", false);

                        // engine name
                        super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                        super.visitLdcInsn("Engine name is: ");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitFieldInsn(Opcodes.GETFIELD, "noppes/npcs/controllers/ScriptContainer", "currentScriptLanguage", "Ljava/lang/String;");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "noppes/npcs/LogWriter", "debug", "(Ljava/lang/String;)V", false);

                        // full code
                        super.visitTypeInsn(Opcodes.NEW, "java/lang/StringBuilder");
                        super.visitInsn(Opcodes.DUP);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "java/lang/StringBuilder", "<init>", "()V", false);
                        super.visitLdcInsn("Full code is as below: \n");
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitMethodInsn(Opcodes.INVOKESPECIAL, "noppes/npcs/controllers/ScriptContainer", "getFullCode", "()Ljava/lang/String;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "append", "(Ljava/lang/String;)Ljava/lang/StringBuilder;", false);
                        super.visitMethodInsn(Opcodes.INVOKEVIRTUAL, "java/lang/StringBuilder", "toString", "()Ljava/lang/String;", false);
                        super.visitMethodInsn(Opcodes.INVOKESTATIC, "noppes/npcs/LogWriter", "debug", "(Ljava/lang/String;)V", false);
                    }
                }
            };
        }
        return mv;
    }
}
