package com.github.mrmks.mc.csf;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;
import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collections;

public class CSFModContainer extends DummyModContainer {
    public CSFModContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();

        meta.name = "CnpcSaveFile";
        meta.modId = "csf";
        meta.version = versionFromStream();
        meta.authorList = Collections.singletonList("MrMks");
        meta.url = "https://github.com/MrMks/CnpcSaveFile";
        meta.logoFile = "";
        meta.description = "A fix mod for CustomNPCs";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }

    private String versionFromStream() {
        try (InputStream stream = getClass().getResourceAsStream("version")) {
            if (stream != null) {
                return IOUtils.toString(stream, StandardCharsets.UTF_8);
            }
        } catch (IOException ignore) {}
        return "unknown";
    }
}
