package course.concurrency.m4_fj.lesson42;

import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.TimeUnit;
import java.util.stream.IntStream;

public class FjpExample {
    public static void main(String[] args) {
        ForkJoinPool forkJoinPool = new ForkJoinPool();
        forkJoinPool.submit(() -> System.out.println(
                IntStream.range(0, 10000000).average().getAsDouble()));
        forkJoinPool.awaitQuiescence(1, TimeUnit.MINUTES);
    }
}
