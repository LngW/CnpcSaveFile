package com.github.mrmks.mc.csf.visitor;

import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;
import org.objectweb.asm.Opcodes;

import java.util.HashMap;
import java.util.Map;

public class EnumPacketServerClassVisitor extends ClassVisitor {
    public EnumPacketServerClassVisitor(int api, ClassVisitor cv) {
        super(api, cv);
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        if ("<init>".equals(name) && "(Lnoppes/npcs/CustomNpcsPermissions$Permission;Z)V".equals(signature)) {
            return new MethodVisitor(api, super.visitMethod(access, name, desc, signature, exceptions)) {
                @Override
                public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
                    if (opcode == Opcodes.INVOKESPECIAL) {
                        super.visitMethodInsn(opcode, owner, name, desc, itf);
                        super.visitVarInsn(Opcodes.ALOAD, 0);
                        super.visitVarInsn(Opcodes.ILOAD, 4);
                        super.visitFieldInsn(Opcodes.PUTFIELD, "noppes/npcs/constants/EnumPacketServer", "needsNpc", "Z");
                    }
                }
            };
        } else if ("<clinit>".equals(name)) {
            return new CinitVisitor(api, super.visitMethod(access, name, desc, signature, exceptions));
        }
        return super.visitMethod(access, name, desc, signature, exceptions);
    }

    private static class CinitVisitor extends MethodVisitor {
        private static final Map<String, String> map = new HashMap<>();
        private static final Map<String, String> map2 = new HashMap<>();
        private static final Map<String, String> map3 = new HashMap<>();
        static {
            map.put("CloneList", "NPC_CLONE");
            map.put("LinkedGetAll","GLOBAL_LINKED");
            map.put("BanksGet", "GLOBAL_BANK");
            map.put("BankGet","GLOBAL_BANK");
            map.put("TransportsGet","GLOBAL_TRANSPORT");
            map.put("TransportCategoriesGet","GLOBAL_TRANSPORT");
            map.put("FactionsGet","GLOBAL_FACTION");
            map.put("FactionGet","GLOBAL_FACTION");
            map.put("NaturalSpawnGet", "GLOBAL_NATURALSPAWN");
            map.put("DoorGet", "TOOL_SCRIPTER");
            map.put("DialogNpcGet", "GLOBAL_DIALOG");
            map.put("RecipesGet", "GLOBAL_RECIPE");
            map.put("RecipeGet", "GLOBAL_RECIPE");
            map.put("QuestOpenGui", "GLOBAL_QUEST");
            map.put("PlayerDataGet", "GLOBAL_PLAYERDATA");
            map.put("RemoteTpToNpc", "NPC_GUI");
            map.put("NaturalSpawnGetAll", "NPC_GUI");
            map.put("MailOpenSetup", "NPC_GUI");
            map.put("DimensionsGet", "TOOL_TELEPORTER");
            map.put("DimensionTeleport", "TOOL_TELEPORTER");
            map.put("SaveTileEntity", "EDIT_BLOCKS");
            map.put("GetTileEntity", "EDIT_BLOCKS");
            map.put("SchematicsTile", "EDIT_BLOCKS");
            map.put("SchematicsSet", "EDIT_BLOCKS");
            map.put("SchematicsBuild", "EDIT_BLOCKS");
            map.put("SchematicsTileSave", "EDIT_BLOCKS");
            map.put("SchematicStore", "EDIT_BLOCKS");

            map2.put("MainmenuAIGet", "NPC_ADVANCED");
            map2.put("MainmenuInvGet", "NPC_INVENTORY");
            map2.put("MainmenuStatsGet", "NPC_STATS");
            map2.put("MainmenuDisplayGet", "NPC_DISPLAY");
            map2.put("MainmenuAdvancedGet", "NPC_ADVANCED");
            map2.put("TransformGet", "NPC_ADVANCED");
            map2.put("TransportGetLocation", "GLOBAL_TRANSPORT");
            map2.put("JobGet", "NPC_ADVANCED");
            map2.put("RoleGet", "NPC_ADVANCED");
            map2.put("MovingPathGet", "TOOL_SCRIPTER");
            map2.put("ScriptDataGet", "TOOL_SCRIPTER");

            map3.put("ScriptBlockDataGet", "TOOL_SCRIPTER");
            map3.put("ScriptDoorDataGet", "TOOL_SCRIPTER");
            map3.put("ScriptPlayerGet", "TOOL_SCRIPTER");
            map3.put("ScriptItemDataGet", "TOOL_SCRIPTER");
            map3.put("ScriptForgeGet", "TOOL_SCRIPTER");
        }

        public CinitVisitor(int api, MethodVisitor mv) {
            super(api, mv);
        }

        private String perm = null;
        private byte type = 0;

        @Override
        public void visitLdcInsn(Object cst) {
            super.visitLdcInsn(cst);
            if (cst instanceof String) {
                String str = (String) cst;
                perm = map.get(str);
                if (perm != null) {
                    type = 0;
                    return;
                }
                perm = map2.get(str);
                if (perm != null) {
                    type = 1;
                    return;
                }
                perm = map3.get(str);
                if (perm != null) {
                    type = 2;
                }
            }
        }

        @Override
        public void visitInsn(int opcode) {
            if (type > 0 && perm != null && (opcode == Opcodes.ICONST_0 || opcode == Opcodes.ICONST_1)) {
                return;
            }
            super.visitInsn(opcode);
        }

        @Override
        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            if (opcode == Opcodes.INVOKESPECIAL && owner.equals("noppes/npcs/constants/EnumPacketServer") && perm != null) {
                super.visitFieldInsn(Opcodes.GETSTATIC, "noppes/npcs/CustomNpcsPermissions", perm, "Lnoppes/npcs/CustomNpcsPermissions$Permission;");
                if (type > 0) {
                    super.visitInsn(type == 1 ? Opcodes.ICONST_1 : Opcodes.ICONST_0);
                }
                super.visitMethodInsn(opcode, owner, name, type == 0 ? "(Ljava/lang/String;ILnoppes/npcs/CustomNpcsPermissions$Permission;)V" : "(Ljava/lang/String;ILnoppes/npcs/CustomNpcsPermissions$Permission;Z)V", false);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }
        }
    }

}
