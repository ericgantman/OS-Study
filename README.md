# Operating Systems Course - Projects

## Course Overview
This repository contains the project assignments for the Operating Systems course. The projects are designed to provide hands-on experience with essential OS concepts such as system calls, process management, inter-process communication (IPC), and synchronization. Projects 1 and 2 are implemented in C, while Project 3 is implemented in Java.

---

## Projects

### 1. Ex1: System Calls, Basic I/O (C)
- **Objective:** 
  - Learn to interact with the operating system using system calls with a focus on basic I/O operations.
- **Description:** 
  - Implement a C program that reads from and writes to files using system calls (`read`, `write`, `open`, `close`, etc.). 
  - Gain experience with file descriptors, non-blocking I/O, and error handling.
- **Key Concepts:** 
  - System calls: `read`, `write`, `open`, `close`
  - File descriptors
  - Error handling and debugging

---

### 2. Ex2: Processes, Multiprocessing & IPC (C)
- **Objective:** 
  - Learn about process creation, control, and inter-process communication (IPC).
- **Description:** 
  - Write a program that uses `fork()` to create child processes and communicate between them using pipes or shared memory. Synchronize the processes and handle termination properly.
- **Key Concepts:**
  - Process creation (`fork()`)
  - Process control (`exec()`, `wait()`)
  - Inter-process communication (pipes, shared memory, or message queues)
  - Basic multiprocessing and synchronization

---

### 3. Ex3: Synchronization (Java)
- **Objective:** 
  - Implement synchronization mechanisms to control access to shared resources in a multithreaded environment.
- **Description:** 
  - Implement a solution to a classic synchronization problem (e.g., producer-consumer or readers-writers problem) using Java synchronization primitives such as `synchronized`, `ReentrantLock`, `Semaphore`, and `Condition`.
- **Key Concepts:**
  - `synchronized` keyword, `ReentrantLock`, `Semaphore`, `Condition`
  - Avoiding race conditions and deadlocks