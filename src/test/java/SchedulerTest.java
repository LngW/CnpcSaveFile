import org.junit.Test;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

public class SchedulerTest {
    @Test
    public void testScheduler() throws InterruptedException {

        UUID uuid = UUID.nameUUIDFromBytes("".getBytes(StandardCharsets.UTF_8));
        System.out.println(uuid);

        uuid = UUID.fromString("c9c843f8-4cb1-4c82-aa61-e264291b7bd6");

        System.out.println(uuid);

    }
}
