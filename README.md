**Multithreaded Apps Faults Detection** 

**Exercise 1: Java Thread Deadlock and Analysis** 

**a) Implementation Task:** 

Java application that demonstrates a deadlock situation involving threads-   solution in DeadlockCase.java

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
   * Locate your Java application (e.g., `DeadlockExample`) in the list of running Java processes.  
   * Double-click the application to open the monitoring interface.  
3. **Obtain a Thread Dump**:  
   * Go to the **Threads** tab.  
   * Click the **Thread Dump** button to take a snapshot of the current state of all threads.

**\-**Programmatically: If you need **detailed information** about thread states and potential **deadlocks**, using `ThreadMXBean` with `getThreadInfo()` and `findDeadlockedThreads()` is very powerful and flexible.If your goal is to simply **print stack traces** without worrying about lock contention, `Thread.getAllStackTraces()` is simpler and more straightforward.

Simple deadlock example in DeadlockDetector.java

1. `ManagementFactory.getThreadMXBean()`:  
   * Retrieves the `ThreadMXBean` instance, which provides access to thread management and information about all threads running in the JVM.  
2. `getThreadInfo()`:  
   * This method returns detailed information about each thread, including its state, stack trace, and lock information (if available).  
   * The call `getThreadInfo(threadMXBean.getAllThreadIds(), Integer.MAX_VALUE)` fetches information for all threads with no limit on the number of stack frames (`Integer.MAX_VALUE`).  
3. `ThreadInfo.toString()`:  
   * The `ThreadInfo` class provides a `toString()` method that includes useful information, such as the thread's state, stack trace, and any locks the thread might be waiting on.

4. `findDeadlockedThreads()` identifies any threads that are deadlocked. If any deadlocks are found, their stack traces are printed.

 **c) Thread Dump Analysis:**   
Analyzing a thread dump is an essential step in identifying deadlocks in a Java application. A thread dump provides a snapshot of all the threads running in the JVM and their current states, including the resources they are waiting for. Here’s how you would go about analyzing a thread dump file obtained from the above deadlock scenario and what specific information to look for:

\- Most thread dumps will have a section that explicitly states a deadlock if one is detected. This line indicates that a deadlock has been detected. The thread dump will then list the threads involved in the deadlock and show their states and the resources they are waiting for.

\- Threads that are part of a deadlock will generally have a state of **`BLOCKED`** or **`WAITING`** (specifically waiting for a monitor or lock). These threads are not doing any work and are waiting for a lock held by another thread, which can indicate a deadlock if they are waiting on each other in a circular fashion. Each thread in the thread dump will have a stack trace. If a thread is waiting for a lock, the stack trace will indicate which resource it is waiting for,  the thread's stack trace will also include the method names and line numbers that show what the thread is currently doing and where it is blocked.

\- Regarding the stack traces, you could also look for synchronized blocks or method calls involving `synchronized` statements or `lock` objects that indicate the thread is waiting for a lock.

\- Look for cycles in the thread dependencies, A deadlock occurs when two or more threads are waiting for resources that each other holds. This creates a circular wait condition.Search for sections in the thread dump that show a cycle, where Thread A is waiting for a lock held by Thread B, and Thread B is waiting for a lock held by Thread A.

Analyzing a thread dump file is also crucial for diagnosing performance bottlenecks. For example threads in the `RUNNABLE` state that show up repeatedly can indicate an application that is busy processing but might be stuck in an infinite loop or consuming high CPU resources. You could also use the stack traces to determine which code is causing the high CPU load.

 **d) Understanding Deadlock:** 

In the context of multithreading, deadlock is a situation where two or more threads become stuck in a state where they are unable to proceed because they are each waiting on resources that are held by the other(s). This results in a cycle of dependencies that cannot be resolved, causing the threads to remain blocked indefinitely. It is a serious issue that can lead to resource wastage, poor performance or application crashes if not handled properly.

A deadlock typically occurs under the following four conditions, known as the deadlock conditions:

Mutual Exclusion: A resource can only be held by one thread at a time. If a thread holds a resource (like a lock, file, or memory region), no other thread can access that resource until the owning thread releases it.

Hold and Wait: A thread holding at least one resource is waiting for additional resources that are currently being held by other threads.

No Preemption: Resources cannot be forcibly taken from threads holding them; they must be released voluntarily.

Circular Wait: A circular chain of threads exists, where each thread is waiting for a resource that the next thread in the chain holds, forming a cycle.

Deadlocks can be avoided or mitigated through various techniques:

**Resource Ordering**: Establish a global order for resource acquisition. Ensure that all threads acquire resources in the same order to avoid circular waits. For example, always acquire `Resource 1` before `Resource 2` in every thread.  
**Timeouts**: Use timeouts when attempting to acquire locks or resources. If a thread cannot acquire a resource within a certain time, it should back off and retry later, or abort the operation.  
**Deadlock Detection**: Regularly check for deadlocks during runtime (such as using thread dump analysis or specialized monitoring tools) and take corrective actions, such as aborting one of the deadlocked threads.  
**Avoid Hold and Wait**: Try to avoid situations where threads hold one resource while waiting for another. Instead, try to acquire all required resources at once.

**Exercise 2: Java Memory Leak and Analysis** 

**a) Implementation Task:** 

Java application that demonstrates memory leaks. solution in MemoryLeakCase.java

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

* Here:  
  * `-Xmx256m`: Sets the maximum heap size to 256MB (you can adjust this as needed).  
  * `-XX:+HeapDumpOnOutOfMemoryError`: Tells the JVM to create a heap dump automatically when an `OutOfMemoryError` occurs.  
  * `-XX:HeapDumpPath=heapdump.hprof`: Specifies the path where the heap dump file (`heapdump.hprof`) will be saved. You can change the file name and path as needed.

#### **2\. Generating a Heap Dump Manually (Using `jmap`):**

If the application is already running, you can generate a heap dump manually by using the `jmap` command, which is a utility that comes with the JDK.

First, identify the **PID (Process ID)** of the running Java application:

`jps`

* This command lists all running Java processes. Find the `PID` of the `MemoryLeakCase` process.  
  Then, use `jmap` to generate the heap dump:

  `jmap -dump:live,format=b,file=heapdump.hprof <PID>`  
* Replace `<PID>` with the process ID of your running Java application. The `-dump` option tells `jmap` to create the heap dump, `format=b` specifies the binary format, and `file=heapdump.hprof` specifies the output file for the heap dump.

#### **3\. Using VisualVM (GUI-based Tool):**

**VisualVM** is a powerful monitoring and troubleshooting tool for Java applications, and it allows you to obtain heap dumps via a graphical interface.

* Start **VisualVM**:  
  If you're using the JDK, VisualVM is included in the `bin` directory, so you can run it using the command:  
  bash  
  Copy code  
  `jvisualvm`  
  1.   
* In VisualVM:  
  1. Connect to the running Java process by selecting it from the list of available applications.  
  2. Go to the **Monitor** tab and check memory usage.  
  3. When you notice the memory usage growing or nearing the limit, click the **Heap Dump** button in the **Profiler** tab or **Monitor** tab to capture a heap dump.  
* The heap dump will be saved in a file that you can analyze later.

#### **4\. Using `jstack` for Thread Dumps (Optional):**

If you're interested in more detailed information about thread activity (for example, if the application is stuck or you suspect deadlock), you can capture a thread dump using `jstack`:

`jstack <PID> > threadDump.txt`

This will print the current stack traces of all threads in the Java process to the `threadDump.txt` file.

**c) Heap Dump Analysis:**  
A **heap dump** provides a snapshot of all the objects in the JVM’s memory at a given point in time. Analyzing the heap dump can help identify **memory leaks** where objects that are no longer needed are still being referenced, causing them to remain in memory and leading to excessive memory usage.

1. **Open the Heap Dump**:  
   * Use a tool like **Eclipse MAT (Memory Analyzer Tool)** or **VisualVM** to open the heap dump file (`heapdump.hprof`).  
2. **Look for Unnecessary Objects Holding Memory**: Memory leaks typically occur when objects that are no longer needed are still referenced, preventing garbage collection. In your case, the `MemoryLeakDemo` example continuously adds new objects to the list, and those objects are not being removed, which can lead to a memory leak.  
   **Specific Information to Look for:**  
   * **Identifying Large Object Retention**:  
     * Use tools like **Eclipse MAT** or **VisualVM** to look at objects consuming large portions of memory. In the case of the `MemoryLeakDemo` app, look for the `MyObject` class, as it holds a large `byte[]` array (1MB). These objects may be unnecessarily retained in memory if they are no longer used.  
     * You can use the **Dominator Tree** in Eclipse MAT to see which objects are retaining the most memory.  
   * **Retained Memory**:  
     * In **Eclipse MAT**, you can look at the **retained heap** to identify objects that are not eligible for garbage collection. These objects are likely being referenced by other objects and are not being removed, indicating a memory leak.  
     * If `MyObject` instances are still present in the heap despite the list (`objectList`) that holds them being discarded or unused, it indicates that the objects are being retained due to ongoing references in memory.  
   * **Dominator Tree**:  
     * The **Dominator Tree** in Eclipse MAT helps you see which objects are retaining other objects. If you see that a large number of `MyObject` instances are retained by a single root object (such as the list `objectList`), it could indicate that the application is not properly cleaning up these objects, leading to a memory leak.  
   * **Object References**:  
     * Look for objects that should have been discarded but still have references, particularly to the `MyObject` instances. If `objectList` is still holding a reference to these `MyObject` instances and is growing without bound, it's a sign of a memory leak.  
3. **Check for Excessive Retained Memory**:  
   * If the heap dump shows that **`MyObject`** instances or similar objects are consuming more and more memory over time (due to the loop continuously adding new objects without clearing the list), it's a strong indication of a memory leak.  
   * Use the **Histogram** feature in **VisualVM** or **Eclipse MAT** to identify which object types are consuming the most memory. If `MyObject` is listed with a large retained size (and its reference count is growing without bounds), this is a clear indication of a memory leak.

### **Key Indicators of Memory Leaks in a Heap Dump:**

1. **Objects Not Garbage Collected**:  
   * Objects that should be removed but are still referenced and held in memory.  
   * In the example application, if `MyObject` instances are continuously created and never removed or cleaned up, they will accumulate in memory.  
2. **Large Objects**:  
   * In the example, the `MyObject` class holds a `byte[]` array of 1MB. If these objects continue to accumulate and aren't cleared, it can cause the application to consume excessive memory, resulting in a memory leak.  
3. **Retained Heap**:  
   * Look for a large number of objects that are being retained in memory but should be discarded. This can be seen in the **Dominator Tree** or **Histogram** views of tools like Eclipse MAT.  
4. **Growing Object List**:  
   * If the list (`objectList`) in `MemoryLeakDemo` keeps growing over time without releasing references, it indicates that these objects are retained unnecessarily, causing a memory leak.

### **Example in Eclipse MAT:**

1. Open the heap dump (`heapdump.hprof`).  
2. Analyze the **Histogram** of objects to look for high memory usage by `MyObject`.  
3. Use the **Dominator Tree** to track which objects are preventing others from being garbage collected.  
4. If `MyObject` is holding large amounts of memory and is still in the heap, it's likely a memory leak.

**d) Avoiding Memory Leaks:** 

A **memory leak** in the context of **multithreading** refers to a situation where a program allocates memory for objects but fails to release it when those objects are no longer needed, causing the program to consume more memory over time. This typically occurs when references to objects are unintentionally retained, preventing the **garbage collector (GC)** from reclaiming the memory.

In a **multithreaded environment**, memory leaks can be more complicated to detect and fix due to the complexity of thread management. Threads can keep references to objects alive, or objects can be retained in **shared caches**, **static variables**, or thread-local storage, which may not be properly cleaned up, even when the thread is finished executing.

### **Causes of Memory Leaks in Multithreaded Applications:**

1. **Unreleased Thread References**: If a thread or thread pool is not properly shut down, it may hold references to objects that would otherwise be garbage collected.  
2. **Static Fields**: Using static fields to store objects across threads can unintentionally prevent garbage collection if the references are not cleared properly.  
3. **Thread Local Variables**: If thread-local variables are not properly removed after a thread completes, these variables may continue to hold objects, leading to memory retention.  
4. **Unfinished or Zombie Threads**: Threads that are started but never properly terminated can hold onto resources like objects in memory.  
5. **Poorly Managed Caches or Data Structures**: In a multithreaded application, objects may be stored in shared collections (e.g., caches or queues), and without proper eviction or cleanup strategies, the memory usage can increase unchecked.

   ### **Strategies to Resolve or Avoid Memory Leaks in Java:**

1. **Proper Thread Management**:  
   * Always ensure threads are properly terminated and not left running indefinitely. Use thread pools (e.g., `ExecutorService`) to manage threads instead of manually creating and managing threads.

   **Example**: If using an `ExecutorService`, always ensure that it is shut down when done:  
     `ExecutorService executor = Executors.newFixedThreadPool(10);`

   `// Submit tasks to the executor`

   `executor.shutdown(); // Properly shuts down the thread pool`

2. **Weak References for Caches**:  
   * Use **weak references** (`WeakReference`, `SoftReference`) when implementing caches. This allows the garbage collector to reclaim objects when memory is low, preventing them from being retained unnecessarily.

   **Example**:  
     `WeakHashMap<KeyType, ValueType> cache = new WeakHashMap<>();`

   `cache.put(new KeyType(), new ValueType());`

   `// Objects in WeakHashMap are eligible for GC when no strong references exist`

3. **Avoid Holding References in Static Fields**:  
   * Static fields can persist across multiple threads and be the cause of memory leaks. Make sure static fields are used sparingly and that they are cleared when no longer needed.

   **Example**:  
     `public class MemoryLeakExample {`

       `private static List<MyObject> sharedList = new ArrayList<>();`

       

       `public static void addToList(MyObject obj) {`

           `sharedList.add(obj); // Uncontrolled growth of sharedList`

       `}`

       

       `public static void clearList() {`

           `sharedList.clear(); // Avoid memory leaks by clearing unused references`

       `}`

   `}`

4. **Proper Use of ThreadLocal Variables**:  
   * Ensure that thread-local variables are removed or nullified when no longer needed. Thread-local variables are often used to store context or data specific to a thread, but failing to clean them up may result in memory leaks.

   **Example**:  
     `private static final ThreadLocal<MyObject> threadLocal = ThreadLocal.withInitial(MyObject::new);`

   

   `public void process() {`

       `// Access and use the thread-local variable`

       `MyObject obj = threadLocal.get();`

       `// After usage, clean it up`

       `threadLocal.remove();  // Important to prevent memory leaks`

   `}`

5. **Using `ExecutorService` for Managing Threads**:  
   * Properly shut down the thread pools created via `ExecutorService` to avoid zombie threads holding resources.

   **Example**:  
     `ExecutorService executor = Executors.newFixedThreadPool(5);`

   `executor.submit(() -> {`

       `// Task implementation`

   `});`

   `executor.shutdown(); // Properly shut down the executor`

6. **Use of `finally` Blocks or Try-with-Resources**:  
   * Always clean up resources (like I/O streams, sockets, database connections) in a `finally` block or using the **try-with-resources** statement to ensure proper cleanup.

   **Example**:

     `try (BufferedReader br = new BufferedReader(new FileReader("file.txt"))) {`

       `String line;`

       `while ((line = br.readLine()) != null) {`

           `// Process the line`

       `}`

   `} catch (IOException e) {`

       `e.printStackTrace();`

   `}`

   `// No need to manually close the BufferedReader, it will be automatically closed`

7. **Memory Profiling and Monitoring**:  
   * Regularly monitor your application for memory leaks using profiling tools like **VisualVM**, **Eclipse MAT (Memory Analyzer Tool)**, or **YourKit**.  
   * You can take a heap dump when you suspect a memory leak, analyze it for objects that shouldn't be retained, and look for uncollected objects that are still in memory.  
8. **Avoid Long-Lived Objects in Thread-Local Storage**:  
   * Avoid using long-lived objects in **ThreadLocal** variables unless absolutely necessary. A typical memory leak scenario occurs when a thread finishes its work but retains references to objects via `ThreadLocal`, preventing garbage collection.  
9. **Regular Cleanup of Caches and Data Structures**:  
   * Implement an eviction strategy for caches (like using **LRU (Least Recently Used) cache**) to ensure that stale objects are removed, avoiding memory buildup.

   **Example**:  
     `LinkedHashMap<Key, Value> cache = new LinkedHashMap<>(16, 0.75f, true) {`

       `@Override`

       `protected boolean removeEldestEntry(Map.Entry<Key, Value> eldest) {`

           `return size() > MAX_CACHE_SIZE;`

       `}};`

   

**Exercise 3: CPU Consumption Implementation and Analysis:** 

**a) Implementation Task:** 

Java application that demonstrates high CPU consumption.

This is a simple example to demonstrate how high CPU consumption can be simulated in a Java application. In a real-world scenario, you may see this kind of behavior in applications with inefficient algorithms or those running tasks that do not yield the CPU.

**b) Monitoring and Analysis:** 

Explain the steps to monitor and analyze the CPU consumption of the above  application. Include any commands or tools that can be used.

**1\. Using Task Manager/** `top`

You can monitor the CPU usage of this application using tools like **Task Manager** on Windows, **top** or **htop** on Linux, or **Activity Monitor** on macOS.

* **Windows**: Open **Task Manager** (Ctrl \+ Shift \+ Esc), go to the **Performance** tab, and observe the CPU usage.  
* **Linux/macOS**: Open a terminal and use the `top` or `htop` command to monitor the system's CPU usage.

### **2\. Using `jps` and `jstack` (Java-specific Tools):**

**Step 1**: Open a terminal window while the application is running.

**Step 2**: Use the **`jps`** command to list Java processes and find the **PID** of your running application:

This will show something like:  
`12345 HighCpuConsumptionDemo`

Where `12345` is the process ID (PID) of your Java application.

**Step 3**: Once you have the PID, you can use **`jstack`** to get the current thread dump. This will show you the active threads and their CPU usage:  
`jstack 12345`

**Step 4**: You can analyze the stack trace to check which thread is consuming the most CPU. Threads stuck in infinite loops (like your application) will likely show repetitive operations in the stack trace.

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

By using the above tools (Task Manager, top, htop, jps, VisualVM, etc.), you can efficiently monitor and analyze the CPU consumption of your Java application. The high CPU consumption will be evident when you see the application using a large percentage of the system’s CPU resources continuously. This can help you identify inefficiencies, such as infinite loops or computationally expensive operations, causing the application to consume excessive CPU.