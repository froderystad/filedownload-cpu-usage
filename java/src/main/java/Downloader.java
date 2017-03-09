import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Downloader {
    private final URL downloadUrl;
    private final Path localFilename;
    private static final int BUFFER_SIZE = 1024;

    private Downloader(URL downloadUrl, Path localFilename) {
        this.downloadUrl = downloadUrl;
        this.localFilename = localFilename;
    }

    public static void main(String[] args) throws IOException {
        URL downloadUrl = new URL(args[0]);
        Path localFilename = Paths.get(args[1]);

        Downloader downloader = new Downloader(downloadUrl, localFilename);
        downloader.execute();
    }

    private void execute() throws IOException {
        long startMillis = System.currentTimeMillis();

        downloadAndSave();

        long timeMillis = System.currentTimeMillis() - startMillis;
        long fileSizeBytes = Files.size(localFilename);
        long downloadSpeedKbps = calculateDownloadSpeedKbps(fileSizeBytes, timeMillis);

        System.out.print("Downloaded and saved " + (fileSizeBytes / 1000) + " kB in " + timeMillis + " ms");
        System.out.println(" (" + downloadSpeedKbps + " kbps)");
    }

    private void downloadAndSave() throws IOException {
        try (BufferedInputStream inStream = new BufferedInputStream(downloadUrl.openStream());
             FileOutputStream outStream = new FileOutputStream(localFilename.toFile())) {

            final byte data[] = new byte[BUFFER_SIZE];
            int numBytesRead;
            while ((numBytesRead = inStream.read(data, 0, BUFFER_SIZE)) != -1) {
                outStream.write(data, 0, numBytesRead);
            }
        }
    }

    /**
     * Calculates download speed in kb/s (kilobits per second).
     */
    static long calculateDownloadSpeedKbps(long numBytes, long timeMillis) {
        return 8 * numBytes / timeMillis;
    }
}
