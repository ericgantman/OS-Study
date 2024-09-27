import java.io.File;

/**
 * A scouter thread This thread lists all sub-directories from a given root
 * path. Each sub-directory is enqueued to be searched for files by Searcher
 * threads.
 * 
 * 
 */
public class Scouter implements Runnable {
    // Root directory for scanning
    private final File root;
    // Synchronized queue for directories
    private final SynchronizedQueue<File> directoryQueue;

    /**
     * Construnctor. Initializes the scouter with a queue for the directories to be
     * searched and a root directory to start from.
     * 
     * @param root           Root directory to start from
     * @param directoryQueue A queue for directories to be searched
     */
    public Scouter(File root, SynchronizedQueue<File> directoryQueue) {
        this.root = root;
        this.directoryQueue = directoryQueue;
    }

    /**
     * Starts the scouter thread. Lists directories under root directory and adds
     * them to queue, then lists directories in the next level and enqueues them and
     * so on. This method begins by registering to the directory queue as a producer
     * and when finishes, it unregisters from it.
     */
    @Override
    public void run() {
        try {
            directoryQueue.registerProducer();
            enqueueDirectories(root);
        } catch (InterruptedException e) {
            // Handle exception with interrupt
            Thread.currentThread().interrupt();
        } finally {
            directoryQueue.unregisterProducer();
        }
    }

    /**
     * Scans all the directories recursively, each directory and its subdirectories
     * and enqueues to the directoryQueue
     * 
     * @param directory current directory to scan the subdirectories
     * @throws InterruptedException handle the interrupted threads
     */
    private void enqueueDirectories(File directory) throws InterruptedException {
        // Get all the subdirectories in the current directory
        File[] subDirs = directory.listFiles(File::isDirectory);
        if (subDirs != null) {
            // Go over the subdirectories
            for (File dir : subDirs) {
                // Enqueue to the directoryQueue
                directoryQueue.enqueue(dir);
                // Recursively enqueue subdirectories in current subdirectory
                enqueueDirectories(dir);
            }
        }
    }
}