package course.concurrency.m4_fj.lesson42;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;

public class FjpExample {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        ForkJoinTask<?> task = forkJoinPool.submit(() -> System.out.println(
                IntStream.range(0, 10000000).average().getAsDouble()));
        task.join();
    }
}
