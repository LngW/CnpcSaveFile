package com.github.mrmks.mc.csf;

import net.minecraftforge.fml.relauncher.IFMLLoadingPlugin;
import org.apache.logging.log4j.LogManager;
import org.jetbrains.annotations.Nullable;

import java.util.Map;

@IFMLLoadingPlugin.TransformerExclusions({"com.github.mrmks.mc.csf"})
@IFMLLoadingPlugin.Name("CnpcSaveFile")
@IFMLLoadingPlugin.MCVersion("1.12.2")
@IFMLLoadingPlugin.SortingIndex(1002)
public class FMLPluginImpl implements IFMLLoadingPlugin {
    @Override
    public String[] getASMTransformerClass() {
        TransformHelper.log = LogManager.getLogger("CnpcSaveFile");
        return new String[] {
                "com.github.mrmks.mc.csf.CnpcClassTransformer"
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
