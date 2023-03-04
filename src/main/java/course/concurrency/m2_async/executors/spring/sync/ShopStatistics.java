package course.concurrency.m2_async.executors.spring.sync;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ShopStatistics {

    private Long totalCount = 0L;
    private Long totalRevenue = 0L;

    public synchronized void addData(Long count, Long price) {
        totalCount += count;
        totalRevenue += (price * count);
        if (Thread.currentThread().getName().equals("main")) {
            return;
        }
        try {
            Thread.sleep(20000L);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public synchronized Long getTotalCount() {
        return totalCount;
    }

    public synchronized Long getTotalRevenue() {
        return totalRevenue;
    }

    @Async
    public synchronized void reset() {
        totalCount = 0L;
        totalRevenue = 0L;
    }
}
