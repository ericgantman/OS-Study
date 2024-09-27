import java.io.File;

/**
 * A searcher thread. Searches for files containing a given pattern and that end
 * with a specific extension in all directories listed in a directory queue.
 */
public class Searcher implements Runnable {
    // Synchronized queue for directories
    private final SynchronizedQueue<File> directoryQueue;
    // Synchronized queue for results
    private final SynchronizedQueue<File> resultsQueue;
    // Pattern to search
    private final String pattern;
    // Extention to search
    private final String extension;

    /**
     * Constructor of Searcher with the given directoryQueue, resutlsQueue, pattern,
     * and extention
     * 
     * @param directoryQueue A queue with directories to search in (as listed by the
     *                       scouter)
     * @param resultsQueue   A queue for files found (to be copied by a copier)
     * @param pattern        Pattern to look for
     * @param extension      wanted extension
     */
    public Searcher(SynchronizedQueue<File> directoryQueue, SynchronizedQueue<File> resultsQueue, String pattern,
            String extension) {
        this.directoryQueue = directoryQueue;
        this.resultsQueue = resultsQueue;
        this.pattern = pattern;
        this.extension = extension;
    }

    /**
     * Runs the searcher thread. Thread will fetch a directory to search in from the
     * directory queue, then search all files inside it (but will not recursively
     * search subdirectories!). Files that a contain the pattern and have the wanted
     * extension are enqueued to the results queue. This method begins by
     * registering to the results queue as a producer and when finishes, it
     * unregisters from it.
     */
    @Override
    public void run() {
        try {
            // Dequeue directories that are given from the directoryQueue to search in them
            File dir;
            while ((dir = directoryQueue.dequeue()) != null) {
                // Search the current directory
                searchDirectory(dir);
            }
        } catch (InterruptedException e) {
            // Handle exception with interrupt
            Thread.currentThread().interrupt();
        } finally {
            resultsQueue.unregisterProducer();
        }
    }

    /**
     * 
     * @param directory The directory to search in
     * @throws InterruptedException
     */
    private void searchDirectory(File directory) throws InterruptedException {
        // Get the list of all the files in the directory
        File[] files = directory.listFiles();
        if (files != null) {
            // Go over all the files
            for (File file : files) {
                // Check if the file matches the pattern and the extention
                if (file.isFile() && file.getName().contains(pattern) && file.getName().endsWith(extension)) {
                    // Enqueue the matched of the search to the resultsQueue
                    resultsQueue.enqueue(file);
                }
            }
        }
    }
}
