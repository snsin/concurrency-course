package course.concurrency.m3_shared.lesson32;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

class TwoMonitorExample {
    private final Object readLock = new Object();
    private final Object writeLock = new Object();
    private int value = 0;

    public static void main(String[] args) {
        var twoMonitorExample = new TwoMonitorExample();
        Callable<Integer> readTask = twoMonitorExample::read;
        var fixedThreadPool = Executors.newFixedThreadPool(4);
        for (int i = 0; i < 1000; i++) {
            int iCopy = i;
            Runnable updateTask = () -> twoMonitorExample.update(iCopy);
            Future<?> submit = fixedThreadPool.submit(updateTask);
            Future<Integer> integerFuture = fixedThreadPool.submit(readTask);
            try {
                submit.get();
                Integer readValue = integerFuture.get();
                if (iCopy != readValue) {
                    System.out.printf("i = %d, readValue = %d\n", iCopy, readValue);
                }
            } catch (InterruptedException | ExecutionException e) {
                throw new RuntimeException(e);
            }
        }
        fixedThreadPool.shutdown();

    }

    public int read() {
        synchronized (readLock) {
            return value;
        }
    }

    public void update(int value) {
        synchronized (writeLock) {
            this.value = value++;
        }
    }
}