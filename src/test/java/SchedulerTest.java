import org.junit.Assert;
import org.junit.Test;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class SchedulerTest {
    @Test
    public void testScheduler() throws InterruptedException {

        /*
        ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
        executor.schedule(() -> System.out.println(Thread.currentThread().getName()), 0, TimeUnit.MILLISECONDS);

        Thread.sleep(50);
         */

        File file = new File("con");
        file.mkdir();

        Assert.assertFalse(file.exists());
        Runnable runnable = () -> {
            try {
                InputStream stream = new FileInputStream(file);
                stream.read();
            } catch (Throwable tr) {}
        };

        Thread thread = new Thread(runnable);

        thread.start();

        Thread.sleep(10);

        thread.interrupt();

        Thread.sleep(10);
    }
}
