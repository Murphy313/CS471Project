package src;

import java.io.*;
import java.util.*;

/**
 * Main class to start the CPU scheduling simulation.
 * This program reads process data from a file, allows the user to select a scheduling policy,
 * and writes the scheduling results to an output file.
 */
public class Main {

    /**
     * The entry point of the program.
     *
     * @param args Command-line arguments (not used).
     * @throws IOException If an I/O error occurs while reading the input file or writing the output file.
     */
    public static void main(String[] args) throws IOException {
        // Path to the input file containing process data.
        String inputPath = "resources/Datafile1-txt.txt";

        // Paths to the output files for different scheduling policies.
        String outputFIFO = "output/output_FIFO.txt";
        String outputSJF = "output/output_SJF.txt";

        // List to store the processes read from the input file.
        List<Process> processes = new ArrayList<>();

        /**
         * Reads process data from the input file.
         * Each line contains arrival time and burst time of a process.
         * The first line (header) is skipped.
         */
        try (BufferedReader reader = new BufferedReader(new FileReader(inputPath))) {
            String line = reader.readLine(); // skip header
            int id = 0;
            while ((line = reader.readLine()) != null && id < 500) {
                String[] parts = line.trim().split("\\s+");
                if (parts.length >= 2) {
                    int arrival = Integer.parseInt(parts[0]);
                    int burst = Integer.parseInt(parts[1]);
                    processes.add(new Process(id++, arrival, burst));
                }
            }
        }

        // Prompt the user to select a scheduling policy.
        Scanner sc = new Scanner(System.in);
        System.out.println("Select Scheduling Policy:\n1. FIFO\n2. SJF (Non-preemptive)");
        int choice = sc.nextInt();

        // Create a Scheduler instance with the list of processes.
        Scheduler scheduler = new Scheduler(processes);

        /**
         * Executes the selected scheduling policy and writes the results to the corresponding output file.
         */
        if (choice == 1) {
            scheduler.runFIFO(outputFIFO);
            System.out.println("Output written to " + outputFIFO);
        } else if (choice == 2) {
            scheduler.runSJF(outputSJF);
            System.out.println("Output written to " + outputSJF);
        } else {
            System.out.println("Invalid selection.");
        }
    }
}
