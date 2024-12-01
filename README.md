**Multithreaded Apps Faults Detection** 

**Exercise 1: Java Thread Deadlock and Analysis** 

**a) Implementation Task:** 

Java application that demonstrates a deadlock situation involving threads- Solution in DeadlockCase.java

 **b) Thread Dump Retrieval:** 

Steps to obtain a thread dump for the above application:

**\-** For simple use cases, the `jstack` or `jcmd` command-line tools are quick and effective. 

Steps to Use `jstack`:

1. **Start your Java application**: Ensure that the `DeadlockCase` application is running.

2. **Find the Process ID (PID)**: Use a command to find the PID of the running Java application. The `jps` command lists Java processes and their PIDs   
   (you could also use `ps -ef | grep DeadlockCase` for Linux/macOS and `tasklist | findstr java` for Windows)

3. **Using `jstack`**: Run `jstack <pid>` where `<pid>` is the process ID of your Java application.

**\-** Using a JVM tool: Tools like VisualVM or JConsole can be used to take a thread dump. **JVisualVM** is a graphical tool that comes with the JDK and can be used to monitor and troubleshoot Java applications.

#### Steps to Use JVisualVM:

1. **Start JVisualVM**: Run `jvisualvm` from the command line or find it in your system's program files.  
2. **Connect to the Application**:  
   * In JVisualVM, go to the **Applications** tab.  
   * Locate your Java application (e.g., `DeadlockCase`) in the list of running Java processes.  
   * Double-click the application to open the monitoring interface.  
3. **Obtain a Thread Dump**:  
   * Go to the **Threads** tab.  
   * Click the **Thread Dump** button to take a snapshot of the current state of all threads.

**\-** Programmatically: If you need detailed information about thread states and potential deadlocks, using `ThreadMXBean` with `getThreadInfo()` and `findDeadlockedThreads()` is very powerful and flexible.If your goal is to simply print stack traces without worrying about lock contention, `Thread.getAllStackTraces()` is simpler and more straightforward.

Simple deadlock example in DeadlockDetector.java using:

1. `ManagementFactory.getThreadMXBean()`: Retrieves the `ThreadMXBean` instance, which provides access to thread management and information about all threads running in the JVM.  
2. `getThreadInfo()`: This method returns detailed information about each thread, including its state, stack trace, and lock information (if available).The call `getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE)` fetches information for all threads with no limit on the number of stack frames (`Integer.MAX_VALUE`).  
3. `ThreadInfo.toString()`: The `ThreadInfo` class provides a `toString()` method that includes useful information, such as the thread's state, stack trace, and any locks the thread might be waiting on.

4. `findDeadlockedThreads()`: Identifies any threads that are deadlocked. If any deadlocks are found, their stack traces are printed.

 **c) Thread Dump Analysis:**   
Analyzing a thread dump is an essential step in identifying deadlocks in a Java application. A thread dump provides a snapshot of all the threads running in the JVM and their current states, including the resources they are waiting for. Here’s how you would go about analyzing a thread dump file obtained from the above deadlock scenario and what specific information to look for:

\- Most thread dumps will have a section that explicitly states a deadlock if one is detected. This line indicates that a deadlock has been detected. The thread dump will then list the threads involved in the deadlock and show their states and the resources they are waiting for.

\- Threads that are part of a deadlock will generally have a state of **`BLOCKED`** or **`WAITING`** (specifically waiting for a monitor or lock). These threads are not doing any work and are waiting for a lock held by another thread, which can indicate a deadlock if they are waiting on each other in a circular fashion. Each thread in the thread dump will have a stack trace. If a thread is waiting for a lock, the stack trace will indicate which resource it is waiting for,  the thread's stack trace will also include the method names and line numbers that show what the thread is currently doing and where it is blocked. In stack traces, you could also look for synchronized blocks or method calls involving `synchronized` statements or `lock` objects that indicate the thread is waiting for a lock.

\- Look for cycles in the thread dependencies, A deadlock occurs when two or more threads are waiting for resources that each other holds. This creates a circular wait condition.Search for sections in the thread dump that show a cycle, where Thread A is waiting for a lock held by Thread B, and Thread B is waiting for a lock held by Thread A.

 **d) Understanding Deadlock:** 

In the context of multithreading, deadlock is a situation where two or more threads become stuck in a state where they are unable to proceed because they are each waiting on resources that are held by the other(s). This results in a cycle of dependencies that cannot be resolved, causing the threads to remain blocked indefinitely. It is a serious issue that can lead to resource wastage, poor performance or application crashes if not handled properly.

A deadlock typically occurs under the following four conditions, known as the deadlock conditions:

Mutual Exclusion: A resource can only be held by one thread at a time. If a thread holds a resource (like a lock, file, or memory region), no other thread can access that resource until the owning thread releases it.

Hold and Wait: A thread holding at least one resource is waiting for additional resources that are currently being held by other threads.

No Preemption: Resources cannot be forcibly taken from threads holding them; they must be released voluntarily.

Circular Wait: A circular chain of threads exists, where each thread is waiting for a resource that the next thread in the chain holds, forming a cycle.

Deadlocks can be avoided or mitigated through various techniques:

**Deadlock Detection**: Regularly check for deadlocks during runtime (such as using thread dump analysis or specialized monitoring tools) and take corrective actions, such as aborting one of the deadlocked threads.

**Avoid Hold and Wait**: Try to avoid situations where threads hold one resource while waiting for another. Instead, try to acquire all required resources at once.

**Lock Hierarchy:** Enforce a **structured order** where locks are acquired in a **hierarchical manner** (higher-level locks before lower-level ones), ensuring no circular dependencies at different levels of the system (similarly, **resource ordering** is used in a global order for linear sequence).

Let's say your application has two resources: `resource1` and `resource2`. You can define a **lock hierarchy** by always ensuring that threads acquire `resource1` first and then `resource2`, never the other way around.
```
public class LockHierarchyExample {

    private final Object resource1 = new Object();

    private final Object resource2 = new Object();
   

    // Thread 1

    public void method1() {

        synchronized (resource1) {    // Always lock resource1 first

            synchronized (resource2) {  // Then lock resource2

                // Access both resources

            }

        }

    }

    // Thread 2

    public void method2() {

        synchronized (resource1) {    // Always lock resource1 first

            synchronized (resource2) {  // Then lock resource2

                // Access both resources

            }

        }

    }

}
```

By enforcing the same order (`resource1` first, then `resource2`), the risk of a **deadlock** is eliminated because no thread will be able to acquire `resource2` before `resource1` (and vice versa).

**Try-Lock Mechanism**: `try-lock` mechanism allows a thread to back out if it cannot acquire the lock within a certain timeframe. You could use **timeouts** when attempting to acquire locks or resources. If a thread cannot acquire a resource within a certain time, it should back off and retry later, or abort the operation.

Here is an example of a program where two threads try to acquire locks in a specific order (resource ordering) using `tryLock` to avoid deadlock:

```
import java.util.concurrent.locks.Lock;  
import java.util.concurrent.locks.ReentrantLock;

public class LockHierarchyWithTryLockExample {

    private final Lock lock1 = new ReentrantLock();  
    private final Lock lock2 = new ReentrantLock();

    // Thread 1  
    public void method1() {  
        try {  
            // Try to acquire lock1  
            if (lock1.tryLock() && lock2.tryLock()) {  
                try {  
                    // Access both resources  
                } finally {  
                    lock1.unlock();  
                    lock2.unlock();  
                }  
            } else {  
                // If locks couldn't be acquired, do something else or retry  
                System.out.println("Could not acquire locks in method1, retrying...");  
            }  
        } catch (Exception e) {  
            // Handle exception  
            e.printStackTrace();  
        }  
    }

    // Thread 2  
    public void method2() {  
        try {  
            // Try to acquire lock1  
            if (lock1.tryLock() && lock2.tryLock()) {  
                try {  
                    // Access both resources  
                } finally {  
                    lock1.unlock();  
                    lock2.unlock();  
                }  
            } else {  
                // If locks couldn't be acquired, do something else or retry  
                System.out.println("Could not acquire locks in method2, retrying...");  
            }  
        } catch (Exception e) {  
            // Handle exception  
            e.printStackTrace();  
        }  
    }

    public static void main(String[] args) {  
        LockHierarchyWithTryLockExample example = new LockHierarchyWithTryLockExample();  
          
        // Simulating thread execution  
        new Thread(example::method1).start();  
        new Thread(example::method2).start();  
    }  
}
```
**Exercise 2: Java Memory Leak and Analysis** 

**a) Implementation Task:** 

Java application that demonstrates memory leaks. Solution in MemoryLeakCase.java

This Java application simulates a memory leak by continuously adding objects to a collection (e.g., a list) without ever removing them. As a result, the objects are not eligible for garbage collection, even though they are no longer needed.

When you run this application, the JVM will continuously allocate memory for the new objects in the `objectList`.  
Since the objects are never removed or set to `null`, the garbage collector cannot reclaim that memory.  
Over time, the application will consume more and more memory, eventually leading to an **OutOfMemoryError**.

Normally, Java has automatic garbage collection, which will reclaim memory used by objects that are no longer referenced. However, in this case, since the objects are still referenced by the `objectList`, the garbage collector cannot free that memory, causing a memory leak.

This example demonstrates a simple and common scenario for a memory leak in Java. In real-world applications, memory leaks often occur when objects are unintentionally retained in long-lived data structures like static collections or caches.

**b) Heap Dump Retrieval:** 

#### **1\. Running the Application with JVM Options:**

The Java Virtual Machine (JVM) provides several options for triggering a heap dump automatically when the JVM runs out of memory.

Use the following JVM options to enable heap dumps on `OutOfMemoryError`:

`java -Xmx256m -XX:+HeapDumpOnOutOfMemoryError -XX:HeapDumpPath=heapdump.hprof MemoryLeakCase`

Here:

* `-Xmx256m`: Sets the maximum heap size to 256MB (you can adjust this as needed).  
  * `-XX:+HeapDumpOnOutOfMemoryError`: Tells the JVM to create a heap dump automatically when an `OutOfMemoryError` occurs.  
  * `-XX:HeapDumpPath=heapdump.hprof`: Specifies the path where the heap dump file (`heapdump.hprof`) will be saved. You can change the file name and path as needed.  
  * Specifies `MemoryLeakCase` to run

#### **2\. Generating a Heap Dump Manually (Using `jmap`):**

If the application is already running, you can generate a heap dump manually by using the `jmap` command, which is a utility that comes with the JDK.

First, identify the **PID (Process ID)** of the running Java application:

`jps`

* This command lists all running Java processes. Find the `PID` of the `MemoryLeakCase` process.  
  Then, use `jmap` to generate the heap dump:

  `jmap -dump:live,format=b,file=heapdump.hprof <PID>`  
* Replace `<PID>` with the process ID of your running Java application. The `-dump` option tells `jmap` to create the heap dump, `format=b` specifies the binary format, and `file=heapdump.hprof` specifies the output file for the heap dump.

**3\. Using `jcmd` (for Java 8u40 and later):**

The `jcmd` utility can also be used to trigger a heap dump in newer versions of Java.

Command to generate a heap dump:  
`jcmd <PID> GC.heap_dump <heap_dump_file_path>`

Replace `<PID>` with the Java process ID and `<heap_dump_file_path>` with the path to store the heap dump file (e.g., `heapdump.hprof`).

**\-Using Eclipse Memory Analyzer Tool (MAT):**

MAT is a powerful tool to analyze heap dumps. While it is primarily used for analyzing heap dumps rather than capturing them, you can use it in conjunction with `jmap` or `jcmd` to analyze the dumps after they are captured.

#### **4\. Using VisualVM (GUI-based Tool):**

**VisualVM** is a powerful monitoring and troubleshooting tool for Java applications, and it allows you to obtain heap dumps via a graphical interface.

* Start **VisualVM**:  
  If you're using the JDK, VisualVM is included in the `bin` directory, so you can run it using the command:

  `jvisualvm`  
* In VisualVM:  
  1. Connect to the running Java process by selecting it from the list of available applications.  
  2. Go to the **Monitor** tab and check memory usage.  
  3. When you notice the memory usage growing or nearing the limit, click the **Heap Dump** button in the **Profiler** tab or **Monitor** tab to capture a heap dump.  
* The heap dump will be saved in a file that you can analyze later.

**c) Heap Dump Analysis:**

A heap dump provides a snapshot of all the objects in the JVM’s memory at a given point in time. Analyzing the heap dump can help identify memory leaks where objects that are no longer needed are still being referenced, causing them to remain in memory and leading to excessive memory usage. To analyze the **heap dump** file obtained from the application and identify a **memory leak**, follow these steps:

1. **Open the Heap Dump**: Use tools like **Eclipse MAT (Memory Analyzer Tool)** or **VisualVM** to load and examine the heap dump file (`heapdump.hprof`).  
2. **Look for Large Objects**: Focus on objects that consume a large portion of memory. In this case, look for instances of `MyObject`, as they hold a large byte array (1MB). If these objects are retained in memory even when no longer needed, it suggests a memory leak.  
3. **Use the Dominator Tree**: The **Dominator Tree** in Eclipse MAT helps identify which objects are preventing others from being garbage collected. If you see many `MyObject` instances still retained by an object (like the `objectList`), it points to a memory leak.  
4. **Check Retained Memory**: Look for objects that have not been garbage collected but should have been. In Eclipse MAT, the **Retained Heap** view shows objects still referenced in memory and not eligible for garbage collection. These retained objects indicate potential leaks.  
5. **Analyze Object References**: If `MyObject` instances are still in memory even though the `objectList` that holds them is no longer in use, it means the objects are being unnecessarily retained, leading to a memory leak.  
6. **Examine the Histogram**: Use the **Histogram** feature in Eclipse MAT or VisualVM to identify which object types consume the most memory. If `MyObject` instances are growing in number and their retained size is increasing, it strongly indicates a memory leak.

### **Key Indicators of a Memory Leak:**

* **Uncollected Objects**: Objects like `MyObject` that should be removed but are still being referenced and retained in memory.  
* **Growing Object List**: The `objectList` in the application continues to grow over time without releasing its references, indicating that the objects are not properly cleaned up.  
* **Large Retained Objects**: Objects like `MyObject` holding large amounts of memory (e.g., 1MB byte array) accumulate over time without being cleared, leading to excessive memory usage.

**d) Avoiding Memory Leaks:** 

A **memory leak** in the context of **multithreading** refers to a situation where a program allocates memory for objects but fails to release it when those objects are no longer needed, causing the program to consume more memory over time. This typically occurs when references to objects are unintentionally retained, preventing the **garbage collector (GC)** from reclaiming the memory.

In a **multithreaded environment**, memory leaks can be more complicated to detect and fix due to the complexity of thread management. Threads can keep references to objects alive, or objects can be retained in **shared caches**, **static variables**, or thread-local storage, which may not be properly cleaned up, even when the thread is finished executing.

### **Strategies to Resolve or Avoid Memory Leaks in Java:**

**1\. Proper Thread Management**:

Always ensure threads are properly terminated and not left running indefinitely. Use thread pools (e.g., `ExecutorService`) to manage threads instead of manually creating and managing threads.

**Example**: If using an `ExecutorService`, always ensure that it is shut down when done:
```
ExecutorService executor = Executors.newFixedThreadPool(10);

// Submit tasks to the executor

executor.shutdown(); // Properly shuts down the thread pool
```

**2\. Weak References for Caches**:

Use **weak references** (`WeakReference`, `SoftReference`) when implementing caches. This allows the garbage collector to reclaim objects when memory is low, preventing them from being retained unnecessarily.

**Example**:

```
WeakHashMap<KeyType, ValueType> cache = new WeakHashMap<>();

cache.put(new KeyType(), new ValueType());

// Objects in WeakHashMap are eligible for GC when no strong references exist
```

**3\. Avoid Holding References in Static Fields**:

Static fields can persist across multiple threads and be the cause of memory leaks. Make sure static fields are used sparingly and that they are cleared when no longer needed.

**Example**:

```
public class MemoryLeakExample {

    private static List<MyObject> sharedList = new ArrayList<>();


    public static void addToList(MyObject obj) {

        sharedList.add(obj); // Uncontrolled growth of sharedList

    }

    
    public static void clearList() {

        sharedList.clear(); // Avoid memory leaks by clearing unused references

    }

}
```

**4\. Proper Use of ThreadLocal Variables**:

Ensure that thread-local variables are removed or nullified when no longer needed. Thread-local variables are often used to store context or data specific to a thread, but failing to clean them up may result in memory leaks.

**Example**:

```
private static final ThreadLocal<MyObject> threadLocal = ThreadLocal.withInitial(MyObject::new);

public void process() {

    // Access and use the thread-local variable

    MyObject obj = threadLocal.get();

    // After usage, clean it up

    threadLocal.remove();  // Important to prevent memory leaks

}
```

**5\. Using `ExecutorService` for Managing Threads**:

Properly shut down the thread pools created via `ExecutorService` to avoid zombie threads holding resources.

**Example**:

```
ExecutorService executor = Executors.newFixedThreadPool(5);

executor.submit(() -> {

    // Task implementation

});

executor.shutdown(); // Properly shut down the executor
```

**6\. Use of `finally` Blocks or Try-with-Resources**:

Always clean up resources (like I/O streams, sockets, database connections) in a `finally` block or using the **try-with-resources** statement to ensure proper cleanup.

**Example**:

```
try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {

    String line;

    while ((line = br.readLine()) != null) {

        // Process the line

    }

} catch (IOException e) {

    `e.printStackTrace();

}

// No need to manually close the BufferedReader, it will be automatically closed
```

**7\. Memory Profiling and Monitoring**:

Regularly monitor your application for memory leaks using profiling tools like **VisualVM**, **Eclipse MAT (Memory Analyzer Tool)**, or **YourKit**.

You can take a heap dump when you suspect a memory leak, analyze it for objects that shouldn't be retained, and look for uncollected objects that are still in memory.

**8\. Avoid Long-Lived Objects in Thread-Local Storage**:

Avoid using long-lived objects in **ThreadLocal** variables unless absolutely necessary. A typical memory leak scenario occurs when a thread finishes its work but retains references to objects via `ThreadLocal`, preventing garbage collection.

**9\. Regular Cleanup of Caches and Data Structures**:

Implement an eviction strategy for caches (like using **LRU (Least Recently Used) cache**) to ensure that stale objects are removed, avoiding memory buildup.

**Example**:

```
LinkedHashMap<Key, Value> cache = new LinkedHashMap<>(16, 0.75f, true) {

    @Override

    protected boolean removeEldestEntry(Map.Entry<Key, Value> eldest) {

        return size() > MAX_CACHE_SIZE;

    }};
```

**Exercise 3: CPU Consumption Implementation and Analysis:** 

**a) Implementation Task:** 

Java application that demonstrates high CPU consumption.Solution in HighCpuConsumptionCase.java

This is a simple example to demonstrate how high CPU consumption can be simulated in a Java application. In a real-world scenario, you may see this kind of behavior in applications with inefficient algorithms or those running tasks that do not yield the CPU.

**b) Monitoring and Analysis:** 

**1\. Using Task Manager/** `top`

You can monitor the CPU usage of this application using tools like **Task Manager** on Windows, **top** or **htop** on Linux, or **Activity Monitor** on macOS.

* **Windows**: Open **Task Manager** (Ctrl \+ Shift \+ Esc), go to the **Performance** tab, and observe the CPU usage.  
* **Linux/macOS**: Open a terminal and use the `top` or `htop` command to monitor the system's CPU usage.

### **2\. Using `jps` and `jstack` (Java-specific Tools):**

**Step 1**: Open a terminal window while the application is running.

**Step 2**: Use the **`jps`** command to list Java processes and find the **PID** of your running application:

This will show something like:  
`12345 HighCpuConsumptionCase`

Where `12345` is the process ID (PID) of your Java application.

**Step 3**: Once you have the PID, you can use **`jstack`** to get the current thread dump. This will show you the active threads and their CPU usage:  
`jstack 12345`

**Step 4**: You can analyze the stack trace to check which thread is consuming the most CPU. Threads stuck in infinite loops (like this application) will likely show repetitive operations in the stack trace.

Analyzing a thread dump file is crucial for diagnosing performance bottlenecks. For example threads in the `RUNNABLE` state that show up repeatedly can indicate an application that is busy processing but might be stuck in an infinite loop or consuming high CPU resources. You could also use the stack traces to determine which code is causing the high CPU load.

### **3\. Using `jvisualvm` (Visual VM):**

**Step 1**: Open **VisualVM**, a GUI tool that comes bundled with the JDK (Java Development Kit). You can launch it by running: `jvisualvm`

**Step 2**: In VisualVM, select the running Java application from the list of processes.

**Step 3**: Go to the **Monitor** tab to see the **CPU usage** graph in real-time.

**Step 4**: You can also use the **Profiler** tab to analyze thread activity and CPU consumption in more detail. This will allow you to pinpoint which methods or threads are using the most CPU.

### **4\. Using `pidstat` (Linux):**

**Step 1**: Get the PID of the running Java process using `jps` or `ps aux`.

**Step 2**: Use `pidstat` to monitor CPU usage by that process:

`pidstat -p <PID> 1`

This will show CPU usage statistics for your application every second.

### **5\. Using `top`/`htop` with Filters (Linux/macOS):**

**Step 1**: You can filter out only the Java process to make the output more readable:  
`top -p <PID>`

**Step 2**: You can also press **`Shift + P`** in `top` or **`F6`** in `htop` to sort processes by CPU usage.

### **Key Metrics to Monitor:**

* **%CPU**: Indicates the percentage of the total CPU time used by the application.  
* **Thread Activity**: In `jstack`, check if threads are repeatedly executing the same function, indicating high CPU usage.  
* **Java Process CPU Usage**: If you're using VisualVM or `top`, keep an eye on how much CPU the Java process is consuming. You might notice spikes or patterns that could indicate performance issues, such as a sudden and sustained high CPU usage caused by a specific thread or process.

By using the above tools, you can efficiently monitor and analyze the CPU consumption of your Java application. The high CPU consumption will be evident when you see the application using a large percentage of the system’s CPU resources continuously. This can help you identify inefficiencies, such as infinite loops or computationally expensive operations, causing the application to consume excessive CPU.
