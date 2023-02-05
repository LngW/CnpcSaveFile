package com.github.mrmks.mc.csf;

import com.google.common.eventbus.EventBus;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

import java.util.Arrays;

public class CSFModContainer extends DummyModContainer {
    public CSFModContainer() {
        super(new ModMetadata());
        ModMetadata meta = getMetadata();

        meta.name = "CnpcSaveFile";
        meta.modId = "csf";
        meta.version = "0.2.1-build28";
        meta.authorList = Arrays.asList("MrMks");
        meta.logoFile = "";
        meta.description = "A fix mod for CustomNPCs";
    }

    @Override
    public boolean registerBus(EventBus bus, LoadController controller) {
        return true;
    }
}
