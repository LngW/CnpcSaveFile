import com.github.mrmks.mc.json.JsonUtil;
import net.minecraft.nbt.*;
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

    @Test
    public void testJsonUtilParseValue() throws IOException, JsonUtil.JsonException {
        NBTBase tag = JsonUtil.convertFrom("127b");
        Assert.assertEquals(1, tag.getId());
        Assert.assertEquals(127, ((NBTTagByte) tag).getByte());

        tag = JsonUtil.convertFrom("-128b");
        Assert.assertEquals(1, tag.getId());
        Assert.assertEquals(-128, ((NBTTagByte)tag).getByte());

        tag = JsonUtil.convertFrom("32767s");
        Assert.assertEquals(2, tag.getId());
        Assert.assertEquals(32767, ((NBTTagShort)tag).getShort());

        tag = JsonUtil.convertFrom("-32768s");
        Assert.assertEquals(2, tag.getId());
        Assert.assertEquals(-32768, ((NBTTagShort)tag).getShort());

        tag = JsonUtil.convertFrom("[I;10086,-10010]");
        Assert.assertEquals(11, tag.getId());
        Assert.assertEquals(10086, ((NBTTagIntArray)tag).getIntArray()[0]);
        Assert.assertEquals(-10010, ((NBTTagIntArray)tag).getIntArray()[1]);

        tag = JsonUtil.convertFrom("\"啊啊啊补背包锟斤拷烫烫烫\"");
        Assert.assertEquals(8, tag.getId());
        Assert.assertEquals("啊啊啊补背包锟斤拷烫烫烫", ((NBTTagString)tag).getString());

        tag = JsonUtil.convertFrom("-1475.000016048d");
        Assert.assertEquals(6, tag.getId());
        Assert.assertEquals(-1475.000016048, ((NBTTagDouble)tag).getDouble(), 0.0000000001);
    }

}
