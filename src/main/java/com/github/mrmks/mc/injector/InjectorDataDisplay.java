package com.github.mrmks.mc.injector;

import com.google.common.collect.Iterables;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import net.minecraft.client.Minecraft;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.StringUtils;
import noppes.npcs.entity.EntityNPCInterface;
import noppes.npcs.entity.data.DataDisplay;

import java.lang.ref.WeakReference;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class InjectorDataDisplay {
    private static final ExecutorService service = Executors.newSingleThreadExecutor();

    public static void loadProfile(EntityNPCInterface npc) {
        GameProfile playerProfile = npc.display.playerProfile;
        if (playerProfile != null && !StringUtils.isNullOrEmpty(playerProfile.getName()) && (!playerProfile.isComplete() || !playerProfile.getProperties().containsKey("textures"))) {
            if (!npc.world.isRemote && npc.getServer() != null) {
                GameProfile gameprofile = npc.getServer().getPlayerProfileCache().getGameProfileForUsername(playerProfile.getName());
                if (gameprofile != null) {
                    npc.display.playerProfile = gameprofile;
                    Property property = (Property) Iterables.getFirst(gameprofile.getProperties().get("textures"), (Object)null);
                    if (property == null) service.submit(new ServerRunnable(npc));
                }
            }
        }
    }

    private static class ClientRunnable implements Runnable {

        WeakReference<EntityNPCInterface> entityRef;
        WeakReference<DataDisplay> displayRef;

        ClientRunnable(EntityNPCInterface npc, DataDisplay display) {
            this.entityRef = new WeakReference<>(npc);
            this.displayRef = new WeakReference<>(display);
        }

        @Override
        public void run() {
            EntityNPCInterface npc = entityRef.get();
            if (npc == null) return;
            MinecraftServer server = npc.getServer();
            if (server == null) return;
            DataDisplay display = displayRef.get();
            if (display == null) return;
            GameProfile profile = display.playerProfile;
            if (profile == null) return;

            profile = Minecraft.getMinecraft().getSessionService().fillProfileProperties(profile, true);

        }
    }

    private static class ServerRunnable implements Runnable {

        WeakReference<EntityNPCInterface> entityRef;
        WeakReference<DataDisplay> displayRef;

        ServerRunnable(EntityNPCInterface npc) {
            this.entityRef = new WeakReference<>(npc);
        }

        @Override
        public void run() {
            EntityNPCInterface npc = entityRef.get();
            if (npc == null) return;
            MinecraftServer server = npc.getServer();
            if (server == null) return;

            GameProfile gameProfile = server.getMinecraftSessionService().fillProfileProperties(npc.display.playerProfile, true);
            server.addScheduledTask(new ServerSchedule(entityRef, gameProfile));
        }
    }

    private static class ServerSchedule implements Runnable {

        private final WeakReference<EntityNPCInterface> ref;
        private final GameProfile profile;
        ServerSchedule(WeakReference<EntityNPCInterface> ref, GameProfile profile) {
            this.ref = ref;
            this.profile = profile;
        }

        @Override
        public void run() {
            EntityNPCInterface npc = ref.get();
            if (npc != null && npc.getServer() != null) {
                npc.updateClient = true;
                npc.display.playerProfile = profile;
            }
        }
    }
}
