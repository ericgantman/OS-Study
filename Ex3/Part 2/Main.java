import java.util.List;

public class Main {

    public static void main(String[] args) {

        List<String> lines = getLinesFromFile();
        System.out.println("Number of lines found: " + lines.size());
        System.out.println("Starting to process");

        long startTimeWithoutThreads = System.currentTimeMillis();
        workWithoutThreads(lines);
        long elapsedTimeWithoutThreads = (System.currentTimeMillis() - startTimeWithoutThreads);
        System.out.println("Execution time: " + elapsedTimeWithoutThreads);

        long startTimeWithThreads = System.currentTimeMillis();
        workWithThreads(lines);
        long elapsedTimeWithThreads = (System.currentTimeMillis() - startTimeWithThreads);
        System.out.println("Execution time: " + elapsedTimeWithThreads);

    }

    private static void workWithThreads(List<String> lines) {
        // Get the number of available cores
        int availableProcessors = Runtime.getRuntime().availableProcessors();
        // Calculate the size of each partition
        int partitionSize = (int) Math.ceil((double) lines.size() / availableProcessors);
        // Create the list of workers
        List<Worker> workers = new java.util.ArrayList<>();
        // Partition the lines and create workers for each partition
        for (int i = 0; i < availableProcessors; i++) {
            // Find the start index for the current partition
            int startIndex = i * partitionSize;
            // Find the end index for the current partition
            int endIndex = Math.min(startIndex + partitionSize, lines.size());
            if (startIndex < endIndex) {
                workers.add(new Worker(lines.subList(startIndex, endIndex)));
            }
        }

        // create a thread pool with the available processors
        java.util.concurrent.ExecutorService executor = java.util.concurrent.Executors
                .newFixedThreadPool(availableProcessors);
        // Submit worker to the thread pool
        for (Worker worker : workers) {
            executor.submit(worker);
        }

        // Shutdown the executor, and wait for all tasks to end
        executor.shutdown();
        try {
            executor.awaitTermination(Long.MAX_VALUE, java.util.concurrent.TimeUnit.NANOSECONDS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    private static void workWithoutThreads(List<String> lines) {
        // run with 1 thread
        Worker worker = new Worker(lines);
        worker.run();
    }

    private static List<String> getLinesFromFile() {
        // Create an ArrayList<String>
        List<String> lines = new java.util.ArrayList<>();
        // Read the shakespeare file provided from C:\Temp\Shakespeare.txt
        // try (java.io.BufferedReader reader = new java.io.BufferedReader(new
        // java.io.FileReader("C:\\Temp\\Shakespeare.txt"))) {
        try (java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.FileReader("./Shakespeare.txt"))) {
            String line;
            while ((line = reader.readLine()) != null) {
                lines.add(line);
            }
        } catch (java.io.IOException e) {
            e.printStackTrace();
        }
        // Return an ArrayList<String> that contains each line read from the file.
        return lines;
    }
}
