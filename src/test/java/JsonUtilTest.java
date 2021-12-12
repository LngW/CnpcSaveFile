import com.github.mrmks.mc.json.JsonUtil;
import net.minecraft.nbt.NBTTagCompound;
import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class JsonUtilTest {

    @Test
    public void testJsonLoadFile() throws IOException, JsonUtil.JsonException {
        File file = new File(Objects.requireNonNull(getClass().getResource("test.json")).getFile());

        NBTTagCompound nbt = JsonUtil.LoadFile(file);

        Assert.assertEquals(1, nbt.getByte("byte"));
        Assert.assertEquals(2, nbt.getShort("short"));
        Assert.assertEquals(3, nbt.getInteger("int"));
        Assert.assertEquals(4, nbt.getLong("long"));
        Assert.assertEquals(5.1, nbt.getFloat("float"), 0.01);
        Assert.assertEquals(6.21, nbt.getDouble("double"), 0.001);
    }
}
