package course.concurrency.exams.auction;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class Notifier {


    ExecutorService threadPool = Executors.newFixedThreadPool(120);

    public void sendOutdatedMessage(Bid bid) {
        threadPool.submit(this::imitateSending);
    }

    private void imitateSending() {
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void shutdown() {
        threadPool.shutdown();
    }
}
