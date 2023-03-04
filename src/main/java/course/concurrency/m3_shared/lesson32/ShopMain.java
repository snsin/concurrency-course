package course.concurrency.m3_shared.lesson32;

import course.concurrency.m2_async.executors.spring.sync.RefundService;
import course.concurrency.m2_async.executors.spring.sync.ShopStatistics;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class ShopMain {
    public static void main(String[] args) {
        var shopStatistics = new ShopStatistics();
        var refundService = new RefundService(shopStatistics);
        var fixedThreadPool = Executors.newFixedThreadPool(4);

        shopStatistics.addData(2L, 20L);
        printStatistics(shopStatistics);
        Future<?> addStatisticsFuture = fixedThreadPool.submit(() -> shopStatistics.addData(1L, 50L));
        Future<?> refundCartFuture = fixedThreadPool.submit(() -> refundService.processRefund(1L));
        try {
            addStatisticsFuture.get();
            printStatistics(shopStatistics);
            refundCartFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }

        printStatistics(shopStatistics);
        fixedThreadPool.shutdown();
    }

    private static void printStatistics(ShopStatistics statistics) {
        System.out.printf("total count: %d; total revenue: %d\n",
                statistics.getTotalCount(),
                statistics.getTotalRevenue());
    }
}
