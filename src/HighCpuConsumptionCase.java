

public class HighCpuConsumptionCase {
	 // Method that runs an infinite loop of calculations to simulate high CPU consumption
    public void simulateHighCpuUsage() {
        while (true) {
            // Perform an intensive CPU-bound calculation (e.g., calculating Fibonacci numbers)
            long fibResult = fibonacci(40);  // Fibonacci of 40 is a large number and computationally expensive
            System.out.println(fibResult);    // Print the result (this is optional, to show something is happening)
            
            // Simulate additional CPU work (this keeps the CPU busy)
            for (long i = 0; i < 1_000_000; i++) {
                Math.sqrt(i); // Just a simple mathematical operation to keep the CPU busy
            }
        }
    }

    // A simple method to calculate the nth Fibonacci number using recursion
    // Fibonacci numbers are computationally expensive due to the recursion
    private long fibonacci(int n) {
        if (n <= 1) {
            return n;
        }
        return fibonacci(n - 1) + fibonacci(n - 2);
    }

    public static void main(String[] args) {
        HighCpuConsumptionCase demo = new HighCpuConsumptionCase();
        System.out.println("Starting high CPU usage simulation...");
        demo.simulateHighCpuUsage(); // Start the high CPU consumption task
    }
}
