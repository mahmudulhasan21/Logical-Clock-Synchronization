# Logical-Clock-Synchronization

Objectives:

I. Write a Sync. Process that does the following:

    a. Maintains the list of all the active processes (address of each process)
    b. Sends the active process list to each new process connected to it.
    c. Periodically checks if the processes are active and discards the entry of dumped
    processes from the list.

II. Write an Worker Process that does the following:

    a. When getting started, each worker process takes a random clock time, gets the list
    of all the processes form the Sync. Process and sends and receives message from
    all the active processes
    b. Uses a loop to simulate clock tick. Sleeps a random time between (50-100ms)
    during each iteration.
    c. Prints the clock upon getting synced each time (when clock time changes after
    receiving a message)
