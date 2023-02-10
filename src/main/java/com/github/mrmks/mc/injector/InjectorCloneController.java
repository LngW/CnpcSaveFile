package com.github.mrmks.mc.injector;

import net.minecraft.nbt.NBTTagCompound;

import java.util.HashMap;
import java.util.Map;

public class InjectorCloneController {

    @SuppressWarnings("unchecked")
    private static final Map<String, NBTTagCompound>[] caches = new Map[10];

    public static NBTTagCompound getCache(String name, int tab) {
        if (tab >= 0 && tab < caches.length) {
            Map<String, NBTTagCompound> map = caches[tab];
            if (map != null) {
                NBTTagCompound cmp = map.get(name);
                return cmp == null ? null : cmp.copy();
            }
        }
        return null;
    }

    public static void setCache(String name, int tab, NBTTagCompound compound) {
        if (tab >= 0 && tab < caches.length) {
            Map<String, NBTTagCompound> map = caches[tab];
            if (compound == null) {
                if (map != null) map.remove(name);
            } else {
                if (map == null) {
                    caches[tab] = map = new HashMap<>();
                }
                map.put(name, compound);
            }
        }
    }

    public static void flushCaches() {
        for (Map<?, ?> map : caches) {
            if (map != null) map.clear();
        }
    }

}
