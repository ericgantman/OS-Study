
import java.io.*;

/**
 * A copier thread. Reads files to copy from a queue and copies them to the
 * given destination.
 */
public class Copier implements Runnable {
    public static final int COPY_BUFFER_SIZE = 4096;

    private File destination;
    private SynchronizedQueue<File> resultsQueue;

    /**
     * Constructor. Initializes the worker with a destination directory and a queue
     * of files to copy.
     * 
     * @param destination  Destination directory
     * @param resultsQueue Queue of files found, to be copied
     */
    public Copier(SynchronizedQueue<File> resultsQueue, File destination) {
        this.destination = destination;
        this.resultsQueue = resultsQueue;
    }

    /**
     * Runs the copier thread. Thread will fetch files from queue and copy them, one
     * after each other, to the destination directory. When the queue has no more
     * files, the thread finishes.
     */
    @Override
    public void run() {
        // Create the destination directory if it doesn't exist
        if (!destination.exists()) {
            destination.mkdirs();
        }
        // Continuously dequeue files from the results queue and copy them to the
        // destination directory
        while (true) {
            File file = resultsQueue.dequeue();
            // If there are no more files to copy, exit the loop
            if (file == null) {
                break;
            }
            // Copy the file to the destination directory
            try {
                copyFile(file, new File(destination, file.getName()));
            } catch (IOException e) {
                System.err.println("Failed to copy file: " + file.getName());
            }
        }
    }

    /**
     * Copies a file from the source to the destination.
     * 
     * @param source Source file
     * @param dest   Destination file
     * @throws IOException If an I/O error occurs
     */
    private void copyFile(File source, File dest) throws IOException {
        try (InputStream in = new FileInputStream(source);
                OutputStream out = new FileOutputStream(dest)) {

            byte[] buffer = new byte[COPY_BUFFER_SIZE];
            int len;
            while ((len = in.read(buffer)) > 0) {
                out.write(buffer, 0, len);
            }
        } catch (IOException e) {
            System.err.println("Failed to copy file: " + source.getName() + ", error message: " + e.getMessage());
        }
    }
}
