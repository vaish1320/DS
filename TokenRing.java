import java.util.concurrent.Semaphore;

public class TokenRing implements Runnable {
    private static final int NUM_PROCESSES = 3; // Number of processes
    private static final int MAX_ROUNDS = 2;    // Total rounds to complete
    private static final Semaphore[] semaphores = new Semaphore[NUM_PROCESSES];
    private static volatile int completedRounds = 0; // Use volatile to ensure visibility across threads

    private final int processId;

    public TokenRing(int processId) {
        this.processId = processId;
    }

    @Override
    public void run() {
        while (true) {
            try {
                semaphores[processId].acquire(); // Wait for the token

                if (completedRounds >= MAX_ROUNDS) {
                    releaseNext(); // Ensure next thread can finish cleanly
                    break;         // Exit the thread
                }

                // Critical Section
                System.out.println("Process id: " + processId + " is in critical section.");
                Thread.sleep(500); // Simulate critical section work
                System.out.println("Process id: " + processId + " is released");

                // Only last process increases the round count
                if (processId == NUM_PROCESSES - 1) {
                    completedRounds++;
                }

                releaseNext(); // Pass token to next process

            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Proper thread interruption
                System.out.println("Thread interrupted: " + processId);
            }
        }
    }

    private void releaseNext() {
        int nextProcess = (processId + 1) % NUM_PROCESSES;
        semaphores[nextProcess].release();
    }

    public static void main(String[] args) {
        // Initialize semaphores: only process 0 can start
        for (int i = 0; i < NUM_PROCESSES; i++) {
            semaphores[i] = new Semaphore(0);
        }
        semaphores[0].release();

        // Start all processes
        for (int i = 0; i < NUM_PROCESSES; i++) {
            new Thread(new TokenRing(i)).start();
        }
    }
}
