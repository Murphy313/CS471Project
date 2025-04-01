package src;

/**
 * Represents a process with arrival time, burst time, and timing metadata.
 * This class provides methods to calculate waiting time, turnaround time, and response time.
 */
public class Process {
    public int id; // Unique identifier for the process.
    public int arrivalTime; // Time at which the process arrives.
    public int burstTime; // Time required by the process to complete execution.
    public int startTime = -1; // Time at which the process starts execution.
    public int completionTime = 0; // Time at which the process completes execution.

    /**
     * Constructs a Process instance with the given ID, arrival time, and burst time.
     *
     * @param id          Unique identifier for the process.
     * @param arrivalTime Time at which the process arrives.
     * @param burstTime   Time required by the process to complete execution.
     */
    public Process(int id, int arrivalTime, int burstTime) {
        this.id = id;
        this.arrivalTime = arrivalTime;
        this.burstTime = burstTime;
    }

    /**
     * Calculates the waiting time for the process.
     * Waiting time is the total time the process spends waiting in the ready queue.
     *
     * @return The waiting time of the process.
     */
    public int getWaitingTime() {
        return getTurnaroundTime() - burstTime;
    }

    /**
     * Calculates the turnaround time for the process.
     * Turnaround time is the total time from arrival to completion.
     *
     * @return The turnaround time of the process.
     */
    public int getTurnaroundTime() {
        return completionTime - arrivalTime;
    }

    /**
     * Calculates the response time for the process.
     * Response time is the time from arrival to the first time the process starts execution.
     *
     * @return The response time of the process.
     */
    public int getResponseTime() {
        return startTime - arrivalTime;
    }
}
