import org.junit.Test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerTest {
    @Test
    public void testScheduler() throws InterruptedException {
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> System.out.println(Thread.currentThread().getName()), 0, TimeUnit.MILLISECONDS);

        Thread.sleep(50);
    }
}
