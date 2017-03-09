import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class DownloaderTest {
    @Test
    public void calculatesDownloadSpeedCorrectly() {
        assertEquals("1 kB in 1 s gives 8 kbps", 8,
                Downloader.calculateDownloadSpeedKbps(1000, 1000));
    }
}
