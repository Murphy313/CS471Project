package src;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.*;

/**
 * Scheduler runs CPU scheduling simulations using FIFO or SJF algorithms.
 * It manages the processes, calculates scheduling metrics, and writes results to output files.
 */
public class Scheduler {
    private final List<Process> processList; // List of processes to be scheduled.
    private final List<Process> completed = new ArrayList<>(); // List of completed processes.
    private final int totalProcesses = 500; // Total number of processes to be scheduled.
    private int currentTime = 0; // Current time in the simulation.
    private int totalBurstTime = 0; // Total burst time of all processes.

    /**
     * Constructs a Scheduler instance with the given list of processes.
     *
     * @param processes List of processes to be scheduled.
     */
    public Scheduler(List<Process> processes) {
        this.processList = new ArrayList<>(processes);
    }

    /**
     * Runs the FIFO (First-In-First-Out) scheduling algorithm.
     * Processes are executed in the order of their arrival times.
     *
     * @param outputPath Path to the output file where the results will be written.
     * @throws IOException If an I/O error occurs while writing the output file.
     */
    public void runFIFO(String outputPath) throws IOException {
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));
        Queue<Process> readyQueue = new LinkedList<>(processList);

        while (completed.size() < totalProcesses) {
            Process p = readyQueue.poll();
            if (currentTime < p.arrivalTime) {
                currentTime = p.arrivalTime;
            }
            p.startTime = currentTime;
            currentTime += p.burstTime;
            p.completionTime = currentTime;
            completed.add(p);
            totalBurstTime += p.burstTime;
        }

        writeStats("FIFO", outputPath);
    }

    /**
     * Runs the SJF (Shortest Job First) scheduling algorithm (non-preemptive).
     * Processes are executed in the order of their burst times, with ties broken by arrival times.
     *
     * @param outputPath Path to the output file where the results will be written.
     * @throws IOException If an I/O error occurs while writing the output file.
     */
    public void runSJF(String outputPath) throws IOException {
        processList.sort(Comparator.comparingInt(p -> p.arrivalTime));
        List<Process> notArrived = new ArrayList<>(processList);
        List<Process> readyQueue = new ArrayList<>();

        while (completed.size() < totalProcesses) {
            // Move processes that have arrived to the ready queue.
            Iterator<Process> it = notArrived.iterator();
            while (it.hasNext()) {
                Process p = it.next();
                if (p.arrivalTime <= currentTime) {
                    readyQueue.add(p);
                    it.remove();
                }
            }

            // If no processes are ready, advance the current time to the next arrival.
            if (readyQueue.isEmpty()) {
                currentTime = notArrived.get(0).arrivalTime;
                continue;
            }

            // Select the process with the shortest burst time.
            readyQueue.sort(Comparator.comparingInt(p -> p.burstTime));
            Process p = readyQueue.remove(0);
            p.startTime = currentTime;
            currentTime += p.burstTime;
            p.completionTime = currentTime;
            completed.add(p);
            totalBurstTime += p.burstTime;
        }

        writeStats("SJF (Non-preemptive)", outputPath);
    }

    /**
     * Writes the scheduling statistics to the specified output file.
     *
     * @param policy The scheduling policy used (e.g., FIFO or SJF).
     * @param path   Path to the output file where the results will be written.
     * @throws IOException If an I/O error occurs while writing the output file.
     */
    private void writeStats(String policy, String path) throws IOException {
        int totalWait = 0, totalTurnaround = 0, totalResponse = 0;

        // Calculate total waiting time, turnaround time, and response time.
        for (Process p : completed) {
            totalWait += p.getWaitingTime();
            totalTurnaround += p.getTurnaroundTime();
            totalResponse += p.getResponseTime();
        }

        // Write the statistics to the output file.
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(path))) {
            writer.write("Scheduling Policy: " + policy + "\n");
            writer.write("Number of processes: " + totalProcesses + "\n");
            writer.write("Total elapsed time: " + currentTime + "\n");
            writer.write(String.format("Throughput: %.2f processes/unit time\n", (float) totalProcesses / currentTime));
            writer.write(String.format("CPU Utilization: %.2f%%\n", (float) totalBurstTime / currentTime * 100));
            writer.write(String.format("Average Waiting Time: %.2f\n", (float) totalWait / totalProcesses));
            writer.write(String.format("Average Turnaround Time: %.2f\n", (float) totalTurnaround / totalProcesses));
            writer.write(String.format("Average Response Time: %.2f\n", (float) totalResponse / totalProcesses));
        }
    }
}
