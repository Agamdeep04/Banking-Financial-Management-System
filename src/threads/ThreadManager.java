
package threads;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ThreadManager {

    private final List<String> queue =
            Collections.synchronizedList(new ArrayList<>());

    private volatile boolean running = true;

    private int raceCounter = 0;
    private int synchronizedCounter = 0;

    public static final String STOP = "STOP";

    public void demonstrateMultithreading() {

        System.out.println("\n--- Multithreading Demonstration ---");

        running = true;
        raceCounter = 0;
        synchronizedCounter = 0;
        queue.clear();

        // Runnable demonstration
        Runnable task = () ->
                System.out.println("Runnable: " +
                        Thread.currentThread().getName());

        Thread t1 = new Thread(task);
        t1.start();

        // start() and run()
        Thread t2 = new Thread(task);
        t2.start();
        t2.run();

        // Daemon thread
        Thread daemon = new Thread(() -> {

            while (running) {
                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            System.out.println("Daemon stopped.");

        });

        daemon.setDaemon(true);
        daemon.start();

        // Race condition
        Thread r1 = new Thread(() -> incrementRace(10000));
        Thread r2 = new Thread(() -> incrementRace(10000));

        r1.start();
        r2.start();

        // Synchronized execution
        Thread s1 = new Thread(() -> incrementSync(10000));
        Thread s2 = new Thread(() -> incrementSync(10000));

        s1.start();
        s2.start();

        try {
            t1.join();
            t2.join();
            r1.join();
            r2.join();
            s1.join();
            s2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        System.out.println("Race counter: " + raceCounter);
        System.out.println("Synchronized counter: " +
                synchronizedCounter);

        // Producer-consumer
        Thread producer = new Thread(() -> {

            for (int i = 1; i <= 5; i++) {

                addToQueue("Transaction-" + i);

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }
            }

            addToQueue(STOP);
            addToQueue(STOP);

        }, "Producer");

        Thread c1 = new Thread(
                new TransactionWorker(queue), "Consumer-1"
        );

        Thread c2 = new Thread(
                new TransactionWorker(queue), "Consumer-2"
        );

        producer.start();
        c1.start();
        c2.start();

        try {
            producer.join();
            c1.join();
            c2.join();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        running = false;

        System.out.println("Multithreading demonstration completed.");
    }

    public void incrementRace(int times) {

        for (int i = 0; i < times; i++) {
            raceCounter++;
        }
    }

    public synchronized void incrementSync(int times) {

        for (int i = 0; i < times; i++) {
            synchronizedCounter++;
        }
    }

    public void addToQueue(String item) {

        synchronized (queue) {

            queue.add(item);

            System.out.println("Produced: " + item);

            queue.notifyAll();
        }
    }
}
