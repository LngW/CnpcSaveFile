package com.github.mrmks.mc.injector;

import com.github.mrmks.mc.json.JsonUtil;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.util.NBTJsonUtil.JsonException;

import java.io.File;
import java.io.IOException;

public class InjectorJsonUtil {
    private static JsonException createException(JsonUtil.JsonException je) {
        throw new UnsupportedOperationException(je.getMessage(), je);
    }

    public static NBTTagCompound LoadFile(File file) throws IOException, JsonException {
        try {
            return JsonUtil.LoadFile(file);
        } catch (JsonUtil.JsonException e) {
            throw createException(e);
        }
    }

    public static void SaveFile(File file, NBTTagCompound tag) throws IOException {
        JsonUtil.SaveFile(file, tag);
    }

    public static NBTTagCompound Convert(String json) throws JsonException {
        try {
            return JsonUtil.Convert(json);
        } catch (JsonUtil.JsonException e) {
            throw createException(e);
        }
    }
}
