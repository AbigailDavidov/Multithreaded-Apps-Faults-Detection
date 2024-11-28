import java.util.ArrayList;
import java.util.List;

public class MemoryLeakCase {
	 // A list that will hold objects
    private List<MyObject> objectList = new ArrayList<>();

    // Method to simulate memory leak by adding objects continuously
    public void createMemoryLeak() {
        while (true) {
            objectList.add(new MyObject()); // Add a new object to the list every time
            // This will cause the program to consume more memory over time without releasing it
            try {
                Thread.sleep(100); // Sleep for a short time to simulate some delay
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    // A simple class that we will add to the list, simulating memory consumption
    private static class MyObject {
        // Some data to make the object non-trivial
        private byte[] largeData = new byte[1024 * 1024 * 10]; // 10MB of data
        
        public MyObject() {
            // Initializing some large data
        }
    }

    public static void main(String[] args) {
    	MemoryLeakCase memoryLeak = new MemoryLeakCase();
        System.out.println("Starting memory leak simulation...");
        memoryLeak.createMemoryLeak(); // Start the memory leak simulation
    	
    	
    }
}
