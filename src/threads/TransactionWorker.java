
package threads;

import java.util.List;
import java.util.concurrent.TimeUnit;

public class TransactionWorker implements Runnable {

    private final List<String> queue;

    public TransactionWorker(List<String> queue) {
        this.queue = queue;
    }

    @Override
    public void run() {

        String name = Thread.currentThread().getName();

        try {

            while (true) {

                String item;

                synchronized (queue) {

                    while (queue.isEmpty()) {
                        queue.wait();
                    }

                    item = queue.remove(0);

                    queue.notifyAll();
                }

                if (ThreadManager.STOP.equals(item)) {
                    break;
                }

                System.out.println(name + " processed " + item);

                TimeUnit.MILLISECONDS.sleep(200);
            }

        } catch (InterruptedException e) {

            Thread.currentThread().interrupt();

            System.out.println(name + " interrupted.");
        }

        System.out.println(name + " terminated.");
    }
}
