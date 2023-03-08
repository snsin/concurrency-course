package course.concurrency.m3_shared.lesson34;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

public class DeadlockExample {

    public static void main(String[] args) {
        var deadlockExample = new DeadlockExample();
        var fixedThreadPool = Executors.newFixedThreadPool(2);
        final Object first = new Object();
        final Object second = new Object();
        final Object third = new Object();
        final Object fourth = new Object();
        Future<?> firstFuture = fixedThreadPool.submit(() -> deadlockExample.task(first, second, third));
        Future<?> secondFuture = fixedThreadPool.submit(() -> deadlockExample.task(third, fourth, second));
        try {
            firstFuture.get();
            secondFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        fixedThreadPool.shutdown();

    }

    public void task(final Object first,
                     final Object second,
                     final Object third) {
        synchronized (first) {
            System.out.printf("thread %s passed first syn\n", Thread.currentThread().getName());
            synchronized (second) {
                System.out.printf("thread %s passed second syn\n", Thread.currentThread().getName());
                try {
                    Thread.sleep(3000);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                synchronized (third) {
                    System.out.printf("thread %s passed third syn\n", Thread.currentThread().getName());
                }
            }
        }
    }
}
