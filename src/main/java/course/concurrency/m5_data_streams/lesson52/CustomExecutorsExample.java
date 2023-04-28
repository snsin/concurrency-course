package course.concurrency.m5_data_streams.lesson52;

import java.util.concurrent.*;
import java.util.concurrent.atomic.LongAdder;

public class CustomExecutorsExample {

    public static final int LIFO_EXECUTOR_TASK_COUNT = 5;
    public static final int REJECTING_EXECUTOR_TASK_COUNT = 1_000_000_000;

    public static void main(String[] args) throws InterruptedException {
        System.out.println("========= lifo executor =========");
        ExecutorService lifoExecutor = createLifoExecutor(1, 1);
        for (int i = 0; i < LIFO_EXECUTOR_TASK_COUNT; i++) {
            final int copy = i;
            lifoExecutor.submit(() ->
                    System.out.println(Thread.currentThread().getName() + " task # " + copy));
        }
        lifoExecutor.shutdown();
        lifoExecutor.awaitTermination(1L, TimeUnit.MINUTES);

        System.out.println();
        System.out.println("========= rejecting executor =========");
        final LongAdder submittedTaskCounter = new LongAdder();
        ThreadPoolExecutor threadPoolExecutor = fixedThreadsRejectingWhenBusyExecutor(8);
        for (int i = 0; i < REJECTING_EXECUTOR_TASK_COUNT; i++) {
            threadPoolExecutor.submit(() -> {
                try {
                    Thread.sleep(1L);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
                submittedTaskCounter.increment();
            });
        }
        threadPoolExecutor.shutdown();
        threadPoolExecutor.awaitTermination(1L, TimeUnit.MINUTES);
        long submitted = submittedTaskCounter.sum();
        System.out.println("tasks submitted: " + submitted
                           + "; tasks rejected: " + (REJECTING_EXECUTOR_TASK_COUNT - submitted));
    }

    private static ThreadPoolExecutor createLifoExecutor(int corePoolSize, int poolSize) {
        if (corePoolSize < 1 || poolSize < 1 || corePoolSize > poolSize) {
            throw new IllegalArgumentException();
        }
        return new ThreadPoolExecutor(corePoolSize,
                poolSize,
                0,
                TimeUnit.MILLISECONDS,
                new TaskStack());
    }

    private static ThreadPoolExecutor fixedThreadsRejectingWhenBusyExecutor(int poolSize) {
        if (poolSize < 1) {
            throw new IllegalArgumentException();
        }
        return new ThreadPoolExecutor(poolSize,
                poolSize,
                0L,
                TimeUnit.MILLISECONDS,
                new SynchronousQueue<>(),
                new ThreadPoolExecutor.DiscardPolicy());
    }

    static private class TaskStack extends LinkedBlockingDeque<Runnable> {

        public TaskStack() {
            super();
        }

        @Override
        public boolean offer(Runnable runnable) {
            return super.offerFirst(runnable);
        }

        @Override
        public boolean offer(Runnable runnable, long timeout, TimeUnit unit) throws InterruptedException {
            return super.offerFirst(runnable, timeout, unit);
        }

    }
}
