import com.github.mrmks.mc.json.JsonUtil;
import net.minecraft.nbt.NBTTagCompound;
import noppes.npcs.util.NBTJsonUtil;
import org.junit.Test;

import java.io.File;
import java.io.IOException;

public class JsonUtilTest {

    @Test
    public void testJsonLoadFile() throws NBTJsonUtil.JsonException, IOException {
        File file = new File("D:\\Workspaces\\Minecraft\\CnpcSaveFile\\run\\saves\\幽灵NPC\\customnpcs\\clones\\1\\Mino Toshiko.json");

        NBTTagCompound nbt = JsonUtil.LoadFile(file);
    }
}
