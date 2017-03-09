import java.io.BufferedInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Downloader {
    private static final int BUFFER_SIZE = 16*1024; // in bytes, e.g. 1024 is 1 KB buffer

    private final URL downloadUrl;
    private final Path localFilename;
    private final DownloadStrategy downloadStrategy;

    private Downloader(URL downloadUrl, Path localFilename) {
        this.downloadUrl = downloadUrl;
        this.localFilename = localFilename;
        this.downloadStrategy = new ChannelDownloadStrategy();
    }

    public static void main(String[] args) throws IOException {
        URL downloadUrl = new URL(args[0]);
        Path localFilename = Paths.get(args[1]);

        Downloader downloader = new Downloader(downloadUrl, localFilename);
        downloader.execute();
    }

    private void execute() throws IOException {
        long startMillis = System.currentTimeMillis();

        downloadStrategy.downloadAndSave();

        long timeMillis = System.currentTimeMillis() - startMillis;
        long fileSizeBytes = Files.size(localFilename);
        long downloadSpeedKbps = calculateDownloadSpeedKbps(fileSizeBytes, timeMillis);

        System.out.print("Downloaded and saved " + (fileSizeBytes / 1000) + " kB in " + timeMillis + " ms");
        System.out.println(" (" + downloadSpeedKbps + " kbps)");
    }

    /**
     * Calculates download speed in kb/s (kilobits per second).
     */
    static long calculateDownloadSpeedKbps(long numBytes, long timeMillis) {
        return 8 * numBytes / timeMillis;
    }

    interface DownloadStrategy {
        void downloadAndSave() throws IOException;
    }

    /**
     * This is the standard data copy strategy found in every text book.
     */
    private class StreamDownloadStrategy implements DownloadStrategy {
        @Override
        public void downloadAndSave() throws IOException {
            try (BufferedInputStream inStream = new BufferedInputStream(downloadUrl.openStream());
                 FileOutputStream outStream = new FileOutputStream(localFilename.toFile())) {

                final byte data[] = new byte[BUFFER_SIZE];
                int numBytesRead;
                while ((numBytesRead = inStream.read(data, 0, BUFFER_SIZE)) != -1) {
                    outStream.write(data, 0, numBytesRead);
                }
            }
        }
    }

    /**
     * I found this, allegedly more efficient strategy here:
     * https://thomaswabner.wordpress.com/2007/10/09/fast-stream-copy-using-javanio-channels/
     */
    private class ChannelDownloadStrategy implements DownloadStrategy {
        @Override
        public void downloadAndSave() throws IOException {
            try (BufferedInputStream inStream = new BufferedInputStream(downloadUrl.openStream());
                 FileOutputStream outStream = new FileOutputStream(localFilename.toFile());
                 ReadableByteChannel inChannel = Channels.newChannel(inStream);
                 WritableByteChannel outChannel = Channels.newChannel(outStream)) {

                final ByteBuffer buffer = ByteBuffer.allocateDirect(BUFFER_SIZE);
                while (inChannel.read(buffer) != -1) {
                    // prepare the buffer to be drained
                    buffer.flip();
                    // write to the channel, may block
                    outChannel.write(buffer);
                    // If partial transfer, shift remainder down
                    // If buffer is empty, same as doing clear()
                    buffer.compact();
                }
                // EOF will leave buffer in fill state
                buffer.flip();
                // make sure the buffer is fully drained.
                while (buffer.hasRemaining()) {
                    outChannel.write(buffer);
                }
            }
        }
    }
}
