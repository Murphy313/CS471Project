import java.io.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

public class ProducerConsumer {

    static final int BUFFER_SIZE = 10;
    static Queue<Integer> buffer = new LinkedList<>();
    static Semaphore empty = new Semaphore(BUFFER_SIZE);
    static Semaphore full = new Semaphore(0);
    static Semaphore mutex = new Semaphore(1);

    static AtomicInteger itemsProduced = new AtomicInteger(0);
    static AtomicInteger itemsConsumed = new AtomicInteger(0);

    static int producerSleepMax;
    static int consumerSleepMax;

    static class Producer extends Thread {
        private int id;
        private Random rand = new Random();

        public Producer(int id) { this.id = id; }

        public void run() {
            try {
                while (true) {
                    int item = rand.nextInt(100);
                    empty.acquire();
                    mutex.acquire();
                    buffer.add(item);
                    itemsProduced.incrementAndGet();
                    System.out.println("Producer " + id + " produced: " + item);
                    mutex.release();
                    full.release();
                    Thread.sleep(rand.nextInt(producerSleepMax + 1));
                }
            } catch (InterruptedException e) {
                // Graceful exit
            }
        }
    }

    static class Consumer extends Thread {
        private int id;
        private Random rand = new Random();

        public Consumer(int id) { this.id = id; }

        public void run() {
            try {
                while (true) {
                    full.acquire();
                    mutex.acquire();
                    int item = buffer.remove();
                    itemsConsumed.incrementAndGet();
                    System.out.println("Consumer " + id + " consumed: " + item);
                    mutex.release();
                    empty.release();
                    Thread.sleep(rand.nextInt(consumerSleepMax + 1));
                }
            } catch (InterruptedException e) {
                // Graceful exit
            }
        }
    }

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

        for (int i = 0; i < numProducers; i++) {
            Thread t = new Producer(i);
            t.start();
            threads.add(t);
        }

        for (int i = 0; i < numConsumers; i++) {
            Thread t = new Consumer(i);
            t.start();
            threads.add(t);
        }

        Thread.sleep(durationSeconds * 1000);

        for (Thread t : threads) {
            t.interrupt();
        }

        for (Thread t : threads) {
            t.join();
        }

        long endTime = System.currentTimeMillis();

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
