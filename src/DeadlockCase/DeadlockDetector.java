package DeadlockCase;
import java.lang.management.ManagementFactory;
import java.lang.management.ThreadMXBean;
import java.io.FileWriter;
import java.io.PrintWriter;

public class DeadlockDetector {

    public static void main(String[] args) throws Exception {
        final Object resource1 = new Object();
        final Object resource2 = new Object();
        
     // Creating the first thread
        Thread thread1 = new Thread(() -> {
            synchronized (resource1) {
                System.out.println("Thread 1: Locked Resource 1");
                try {
                    // Simulate some work with Resource 1
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Thread 1: Waiting to lock Resource 2");
                synchronized (resource2) {
                    System.out.println("Thread 1: Locked Resource 2");
                }
            }
        });

        // Creating the second thread
        Thread thread2 = new Thread(() -> {
            synchronized (resource2) {
                System.out.println("Thread 2: Locked Resource 2");
                try {
                    // Simulate some work with Resource 2
                    Thread.sleep(50);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                System.out.println("Thread 2: Waiting to lock Resource 1");
                synchronized (resource1) {
                    System.out.println("Thread 2: Locked Resource 1");
                }
            }
        });

        thread1.start();
        thread2.start();
        

        // Deadlock Detection - check immediately
        ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();

        while (true) {
            // Detect if deadlock exists
            long[] deadlockedThreads = threadMXBean.findDeadlockedThreads();
            if (deadlockedThreads != null) {
                try (PrintWriter writer = new PrintWriter(new FileWriter("deadlock-dump.txt"))) {
                    writer.println("Deadlock detected!");
                    for (long threadId : deadlockedThreads) {
                        writer.println(threadMXBean.getThreadInfo(threadId));
                    }
                }
                break;  // Deadlock detected, stop monitoring
            }
            Thread.sleep(100); // Check periodically, without blocking too long
        }

        System.out.println("Deadlock information written to 'deadlock-dump.txt'");
    }
}
