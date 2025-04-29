import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * A Producer-Consumer implementation using Semaphores for synchronization.
 * Producers generate items and add them to a shared buffer, while Consumers
 * retrieve items from the buffer. The program tracks the number of items
 * produced and consumed and generates a summary report.
 */
public class ProducerConsumer {

    /** The maximum size of the buffer. */
    static final int BUFFER_SIZE = 10;

    /** The shared buffer for storing items. */
    static Queue<Integer> buffer = new LinkedList<>();

    /** Semaphore to track available empty slots in the buffer. */
    static Semaphore empty = new Semaphore(BUFFER_SIZE);

    /** Semaphore to track available filled slots in the buffer. */
    static Semaphore full = new Semaphore(0);

    /** Semaphore to ensure mutual exclusion when accessing the buffer. */
    static Semaphore mutex = new Semaphore(1);

    /** Counter for the total number of items produced. */
    static AtomicInteger itemsProduced = new AtomicInteger(0);

    /** Counter for the total number of items consumed. */
    static AtomicInteger itemsConsumed = new AtomicInteger(0);

    /** Maximum sleep time for producers in milliseconds. */
    static int producerSleepMax;

    /** Maximum sleep time for consumers in milliseconds. */
    static int consumerSleepMax;

    /**
     * Producer thread that generates items and adds them to the buffer.
     */
    static class Producer extends Thread {
        private int id;
        private Random rand = new Random();

        /**
         * Constructs a Producer with a unique ID.
         * @param id The ID of the producer.
         */
        public Producer(int id) { this.id = id; }

        /**
         * Continuously produces items and adds them to the buffer.
         */
        public void run() {
            try {
                while (true) {
                    int item = rand.nextInt(100); // Generate a random item
                    empty.acquire(); // Wait for an empty slot
                    mutex.acquire(); // Enter critical section
                    buffer.add(item); // Add item to the buffer
                    itemsProduced.incrementAndGet(); // Increment produced count
                    System.out.println("Producer " + id + " produced: " + item);
                    mutex.release(); // Exit critical section
                    full.release(); // Signal a filled slot
                    Thread.sleep(rand.nextInt(producerSleepMax + 1)); // Simulate work
                }
            } catch (InterruptedException e) {
                // Graceful exit
            }
        }
    }

    /**
     * Consumer thread that retrieves items from the buffer.
     */
    static class Consumer extends Thread {
        private int id;
        private Random rand = new Random();

        /**
         * Constructs a Consumer with a unique ID.
         * @param id The ID of the consumer.
         */
        public Consumer(int id) { this.id = id; }

        /**
         * Continuously consumes items from the buffer.
         */
        public void run() {
            try {
                while (true) {
                    full.acquire(); // Wait for a filled slot
                    mutex.acquire(); // Enter critical section
                    int item = buffer.remove(); // Remove item from the buffer
                    itemsConsumed.incrementAndGet(); // Increment consumed count
                    System.out.println("Consumer " + id + " consumed: " + item);
                    mutex.release(); // Exit critical section
                    empty.release(); // Signal an empty slot
                    Thread.sleep(rand.nextInt(consumerSleepMax + 1)); // Simulate work
                }
            } catch (InterruptedException e) {
                // Graceful exit
            }
        }
    }

    /**
     * The main method to start the Producer-Consumer simulation.
     * @param args Command-line arguments: number of producers, number of consumers,
     *             duration in seconds, producer sleep max in ms, and consumer sleep max in ms.
     * @throws InterruptedException If the main thread is interrupted.
     */
    public static void main(String[] args) throws InterruptedException {
        if (args.length < 5) {
            System.out.println("Usage: java ProducerConsumer <numProducers> <numConsumers> <durationSeconds> <producerSleepMax> <consumerSleepMax>");
            return;
        }

        int numProducers = Integer.parseInt(args[0]);
        int numConsumers = Integer.parseInt(args[1]);
        int durationSeconds = Integer.parseInt(args[2]);
        producerSleepMax = Integer.parseInt(args[3]);
        consumerSleepMax = Integer.parseInt(args[4]);

        List<Thread> threads = new ArrayList<>();
        long startTime = System.currentTimeMillis();

        // Start producer threads
        for (int i = 0; i < numProducers; i++) {
            Thread t = new Producer(i);
            t.start();
            threads.add(t);
        }

        // Start consumer threads
        for (int i = 0; i < numConsumers; i++) {
            Thread t = new Consumer(i);
            t.start();
            threads.add(t);
        }

        // Run the simulation for the specified duration
        Thread.sleep(durationSeconds * 1000);

        // Interrupt all threads to stop them
        for (Thread t : threads) {
            t.interrupt();
        }

        // Wait for all threads to finish
        for (Thread t : threads) {
            t.join();
        }

        long endTime = System.currentTimeMillis();

        // Generate a summary report
        try (PrintWriter writer = new PrintWriter(new FileWriter("report.txt", true))) {
            writer.println("========= SUMMARY =========");
            writer.println("Producers: " + numProducers);
            writer.println("Consumers: " + numConsumers);
            writer.println("Duration: " + durationSeconds + " seconds");
            writer.println("Producer Sleep Max: " + producerSleepMax + " ms");
            writer.println("Consumer Sleep Max: " + consumerSleepMax + " ms");
            writer.println("Items Produced: " + itemsProduced.get());
            writer.println("Items Consumed: " + itemsConsumed.get());
            writer.println("Turnaround Time: " + (endTime - startTime) + " ms");
            writer.println("===========================\n");
        } catch (IOException e) {
            System.err.println("Error writing to report.txt: " + e.getMessage());
        }
    }
}
