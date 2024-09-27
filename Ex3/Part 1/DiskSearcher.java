import java.io.File;

public class DiskSearcher {

    /**
     * Capacity of the queue that holds the directories to be searched.
     */
    public static final int DIRECTORY_QUEUE_CAPACITY = 50;

    /**
     * Capacity of the queue that holds the files found.
     */
    public static final int RESULTS_QUEUE_CAPACITY = 50;

    /**
     * Constructor.
     */
    public DiskSearcher() {
    }

    /**
     * Main method. Reads arguments from command line and starts the search.
     *
     * @param args Command line arguments
     */
    public static void main(String[] args) {
        if (args.length != 6) {
            System.err.println(
                    "Usage: java DiskSearcher <filename-pattern> <file-extension> <root-directory> <destination-directory> <# of searchers> <# of copiers>");
            System.exit(1);
        }

        String filenamePattern = args[0];
        String fileExtension = args[1];
        File rootDirectory = new File(args[2]);
        File destinationDirectory = new File(args[3]);
        int numberOfSearchers = Integer.parseInt(args[4]);
        int numberOfCopiers = Integer.parseInt(args[5]);

        // Validate directories
        if (!rootDirectory.isDirectory()) {
            System.err.println("Invalid root directory: " + rootDirectory.getPath());
            System.exit(1);
        }
        if (!destinationDirectory.exists() && !destinationDirectory.mkdirs()) {
            System.err.println("Failed to create destination directory: " +
                    destinationDirectory.getPath());
            System.exit(1);
        }
        // Create synchronized queues
        SynchronizedQueue<File> directoryQueue = new SynchronizedQueue<>(DIRECTORY_QUEUE_CAPACITY);
        SynchronizedQueue<File> resultsQueue = new SynchronizedQueue<>(RESULTS_QUEUE_CAPACITY);
        // Start scouter thread
        Thread scouterThread = new Thread(new Scouter(rootDirectory, directoryQueue));
        scouterThread.start();
        // Start searcher threads
        Thread[] searcherThreads = new Thread[numberOfSearchers];
        for (int i = 0; i < numberOfSearchers; i++) {
            resultsQueue.registerProducer(); // Register each searcher as a producer
            searcherThreads[i] = new Thread(new Searcher(directoryQueue, resultsQueue, filenamePattern, fileExtension));
            searcherThreads[i].start();
        }
        // Start copier threads
        Thread[] copierThreads = new Thread[numberOfCopiers];
        for (int i = 0; i < numberOfCopiers; i++) {
            copierThreads[i] = new Thread(new Copier(resultsQueue, destinationDirectory));
            copierThreads[i].start();
        }
        // Wait for scouter thread to finish
        try {
            scouterThread.join();
            for (Thread searcherThread : searcherThreads) {
                searcherThread.join();
            }
            for (Thread copierThread : copierThreads) {
                copierThread.join();
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
