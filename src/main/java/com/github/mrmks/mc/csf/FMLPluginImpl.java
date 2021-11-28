package com.github.mrmks.mc.csf;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({"com.github.mrmks.mc.csf"})
@IFMLLoadingPlugin.Name("CnpcSaveFile")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1002)
public class FMLPluginImpl implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        return new String[]{
                "com.github.mrmks.mc.csf.CnpcSaveFileTransformer",
                "com.github.mrmks.mc.csf.JsonUtilCreateExceptionTransformer",
                "com.github.mrmks.mc.csf.CnpcJsonExceptionTransformer"
        };
    }

    @Override
    public String getModContainerClass() {
        return null;
    }

    @Nullable
    @Override
    public String getSetupClass() {
        return null;
    }

    @Override
    public void injectData(Map<String, Object> data) {

    }

    @Override
    public String getAccessTransformerClass() {
        return null;
    }
}
