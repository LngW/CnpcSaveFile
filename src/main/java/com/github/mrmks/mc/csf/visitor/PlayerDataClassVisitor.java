package com.github.mrmks.mc.csf.visitor;

import net.minecraftforge.fml.relauncher.FMLLaunchHandler;
import org.lwjgl.openal.AL;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.Label;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class PlayerDataClassVisitor extends ClassVisitor {

    private final boolean[] f = new boolean[]{false, false, false, false};

    public PlayerDataClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        super.visit(version, access, name, signature, superName, interfaces);
        this.cv.visitField(ACC_PRIVATE | ACC_STATIC | ACC_FINAL, "savingMap", "Ljava/util/Map;", "Ljava/util/Map<Ljava/lang/String;Lnet/minecraft/nbt/NBTTagCompound;>;", null).visitEnd();
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if (!f[0] && "save".equals(name) && "(Z)V".equals(desc)) {
            f[0] = true;
            return new SaveVisitor(api, super.visitMethod(access, name, desc, signature, exceptions));
        } else if (!f[1] && "get".equals(name) && "(Lnet/minecraft/entity/player/EntityPlayer;)Lnoppes/npcs/controllers/data/PlayerData;".equals(desc)) {
            f[1] = true;
            return new GetVisitor(api, super.visitMethod(access, name, desc, signature, exceptions));
        } else if (!f[2] && "lambda$save$0".equals(name)) {
            f[2] = true;
            return new Lambda$0Visitor(api, super.visitMethod(access, name, desc, signature, exceptions));
        } else if (!f[3] && "<clinit>".equals(name)) {
            f[3] = true;
            return new CLInitVisitor(api, super.visitMethod(access, name, desc, signature, exceptions));
        } else return super.visitMethod(access, name, desc, signature, exceptions);
    }

    private static class SaveVisitor extends MethodVisitor {
        private boolean f = false;
        public SaveVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitVarInsn(int opcode, int var) {
            if (!f && opcode == ASTORE && var == 2) {
                super.visitVarInsn(opcode, var);
                f = true;
                mv.visitFieldInsn(GETSTATIC, "noppes/npcs/controllers/data/PlayerData", "savingMap", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitFieldInsn(GETFIELD, "noppes/npcs/controllers/data/PlayerData", "uuid", "Ljava/lang/String;");
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitInsn(POP);



//                mv.visitInsn(MONITORENTER);
//                Label label0 = new Label();
//                mv.visitLabel(label0);
//                mv.visitFieldInsn(GETSTATIC, "noppes/npcs/controllers/data/PlayerData", "savingMap", "Ljava/util/HashMap;");
//                mv.visitVarInsn(ALOAD, 0);
//                mv.visitFieldInsn(GETFIELD, "noppes/npcs/controllers/data/PlayerData", "uuid", "Ljava/lang/String;");
//                mv.visitVarInsn(ALOAD, 2);
//                mv.visitMethodInsn(INVOKEVIRTUAL, "java/util/HashMap", "put", "(Ljava/lang/Object;Ljava/lang/Object;)Ljava/lang/Object;", false);
//                mv.visitInsn(POP);
//                mv.visitVarInsn(ALOAD, 3);
//                mv.visitInsn(MONITOREXIT);

//                Label label1 = new Label();
//                Label label4 = new Label();
//                mv.visitLabel(label1);
//                mv.visitJumpInsn(GOTO, label4);
//                Label label2 = new Label();
//                mv.visitLabel(label2);
//                mv.visitFrame(F_FULL, 4, new Object[]{"noppes/npcs/controllers/data/PlayerData", INTEGER, "net/minecraft/nbt/NBTTagCompound", "java/lang/Object"}, 1, new Object[]{"java/lang/Throwable"});
//                mv.visitVarInsn(ASTORE, 4);
//                mv.visitVarInsn(ALOAD, 3);
//                mv.visitInsn(MONITOREXIT);
//                Label label3 = new Label();
//                mv.visitLabel(label3);
//                mv.visitVarInsn(ALOAD, 4);
//                mv.visitInsn(ATHROW);
//                mv.visitLabel(label4);
//                mv.visitFrame(F_CHOP, 1, null, 0, null);
//                mv.visitTryCatchBlock(label2, label3, label2, null);
            } else super.visitVarInsn(opcode, var);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            super.visitMaxs(maxStack + 1, maxLocals);
        }
    }

    private static class GetVisitor extends MethodVisitor {
        private final boolean[] f = new boolean[]{false, false};
        private Label label = new Label();
        private Label label_1 = new Label();
        public GetVisitor(int api, MethodVisitor mv) {
            super(api, mv);

        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (!f[0] && "java/util/UUID".equals(owner) && "toString".equals(name)) {
                f[0] = true;

                super.visitMethodInsn(opcode, owner, name, desc, itf);
                mv.visitLabel(label_1);
                mv.visitVarInsn(ASTORE, 3);
//                mv.visitInsn(ACONST_NULL);
//                mv.visitVarInsn(ASTORE, 2);

//                mv.visitFieldInsn(GETSTATIC, "noppes/npcs/controllers/data/PlayerData", "savingMap", "Ljava/util/Map;");
//                mv.visitInsn(DUP);
//                mv.visitVarInsn(ASTORE, 4);
//                mv.visitInsn(MONITORENTER);
//                Label label0 = new Label();
//                mv.visitLabel(label0);
                mv.visitFieldInsn(GETSTATIC, "noppes/npcs/controllers/data/PlayerData", "savingMap", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 3);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "get", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitTypeInsn(CHECKCAST, "net/minecraft/nbt/NBTTagCompound");
                mv.visitVarInsn(ASTORE, 2);
//                mv.visitVarInsn(ALOAD, 4);
//                mv.visitInsn(MONITOREXIT);
//                Label label1 = new Label();
//                Label label4 = new Label();
//                mv.visitLabel(label1);
//                mv.visitJumpInsn(GOTO, label4);
//                Label label2 = new Label();
//                mv.visitLabel(label2);
//                mv.visitFrame(F_FULL, 5, new Object[]{"net/minecraft/entity/player/EntityPlayer", "noppes/npcs/controllers/data/PlayerData", "net/minecraft/nbt/NBTTagCompound", "java/lang/String", "java/lang/Object"}, 1, new Object[]{"java/lang/Throwable"});
//                mv.visitVarInsn(ASTORE, 5);
//                mv.visitVarInsn(ALOAD, 4);
//                mv.visitInsn(MONITOREXIT);
//                Label label3 = new Label();
//                mv.visitLabel(label3);
//                mv.visitVarInsn(ALOAD, 5);
//                mv.visitInsn(ATHROW);
//                mv.visitLabel(label4);
//                mv.visitFrame(F_CHOP, 1, null, 0, null);
//                mv.visitTryCatchBlock(label0, label1, label2, null);
//                mv.visitTryCatchBlock(label2, label3, label2, null);

                mv.visitVarInsn(ALOAD, 2);
                Label label5 = new Label();
                Label label6 = new Label();
                mv.visitJumpInsn(IFNONNULL, label5);
                mv.visitJumpInsn(GOTO, label6);
                mv.visitLabel(label5);
                mv.visitFrame(F_APPEND, 3, new Object[]{"noppes/npcs/controllers/data/PlayerData", "net/minecraft/nbt/NBTTagCompound", "java/lang/String"}, 0, null);
                mv.visitVarInsn(ALOAD, 2);
                mv.visitMethodInsn(INVOKEVIRTUAL, "net/minecraft/nbt/NBTTagCompound", FMLLaunchHandler.isDeobfuscatedEnvironment() ? "copy" : "func_74737_b", "()Lnet/minecraft/nbt/NBTTagCompound;", false);
                mv.visitVarInsn(ASTORE, 2);
                mv.visitJumpInsn(GOTO, label);
                mv.visitLabel(label6);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                mv.visitVarInsn(ALOAD, 3);
            } else if (!f[1] && "noppes/npcs/controllers/data/PlayerData".equals(owner) && "setNBT".equals(name)) {
                f[1] = true;
                mv.visitInsn(POP2);
                mv.visitLabel(label);
                mv.visitFrame(F_SAME, 0, null, 0, null);
                mv.visitVarInsn(ALOAD, 1);
                mv.visitVarInsn(ALOAD, 2);
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            } else super.visitMethodInsn(opcode, owner, name, desc, itf);
        }

        @Override
        public void visitMaxs(int maxStack, int maxLocals) {
            mv.visitLocalVariable("uuid", "Ljava/lang/String;", null, label_1, label, 3);
            super.visitMaxs(maxStack, maxLocals + 1);
            label_1 = label = null;
        }

        @Override
        public void visitFrame(int type, int nLocal, Object[] local, int nStack, Object[] stack) {
            if (type == F_APPEND && f[0] && !f[1]) {
                super.visitFrame(F_CHOP, 1, null, 0, null);
            } else super.visitFrame(type, nLocal, local, nStack, stack);
        }
    }

    private static class Lambda$0Visitor extends MethodVisitor {
        private boolean f = false;
        public Lambda$0Visitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitInsn(int opcode) {
            if (!f && opcode == RETURN) {
                f = true;
//                mv.visitFieldInsn(GETSTATIC, "noppes/npcs/controllers/data/PlayerData", "savingMap", "Ljava/util/Map;");
//                mv.visitInsn(DUP);
//                mv.visitVarInsn(ASTORE, 2);
//                mv.visitInsn(MONITORENTER);
                Label label0 = new Label();
                mv.visitLabel(label0);
                mv.visitFieldInsn(GETSTATIC, "noppes/npcs/controllers/data/PlayerData", "savingMap", "Ljava/util/Map;");
                mv.visitVarInsn(ALOAD, 0);
                mv.visitInsn(ICONST_0);
                mv.visitVarInsn(ALOAD, 0);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "length", "()I", false);
                mv.visitInsn(ICONST_5);
                mv.visitInsn(ISUB);
                mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/String", "substring", "(II)Ljava/lang/String;", false);
                mv.visitMethodInsn(INVOKEINTERFACE, "java/util/Map", "remove", "(Ljava/lang/Object;)Ljava/lang/Object;", true);
                mv.visitInsn(POP);
//                mv.visitVarInsn(ALOAD, 2);
//                mv.visitInsn(MONITOREXIT);
//                Label label1 = new Label();
//                Label label4 = new Label();
//                mv.visitLabel(label1);
//                mv.visitJumpInsn(GOTO, label4);
//                Label label2 = new Label();
//                mv.visitLabel(label2);
//                mv.visitFrame(F_FULL, 3, new Object[]{"java/lang/String", "net/minecraft/nbt/NBTTagCompound", "java/lang/Object"}, 1, new Object[]{"java/lang/Throwable"});
//                mv.visitVarInsn(ASTORE, 3);
//                mv.visitVarInsn(ALOAD, 2);
//                mv.visitInsn(MONITOREXIT);
//                Label label3 = new Label();
//                mv.visitLabel(label3);
//                mv.visitVarInsn(ALOAD, 3);
//                mv.visitInsn(ATHROW);
//                mv.visitLabel(label4);
//                mv.visitFrame(F_CHOP, 1, null, 0, null);
//                mv.visitTryCatchBlock(label0, label1, label2, null);
//                mv.visitTryCatchBlock(label2, label3, label2, null);
            }
            super.visitInsn(opcode);
        }
    }

    private static class CLInitVisitor extends MethodVisitor {
        private boolean f = false;
        public CLInitVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        @Override
        public void visitCode() {
            if (!f) {
                f = true;
                super.visitCode();
                visitLabel(new Label());
                visitTypeInsn(NEW, "java/util/concurrent/ConcurrentHashMap");
                visitInsn(DUP);
                visitMethodInsn(INVOKESPECIAL, "java/util/concurrent/ConcurrentHashMap", "<init>", "()V", false);
                visitFieldInsn(PUTSTATIC, "noppes/npcs/controllers/data/PlayerData", "savingMap", "Ljava/util/Map;");
            } else super.visitCode();
        }
    }

}
