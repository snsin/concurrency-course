package course.concurrency.m4_fj.lesson42;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.IntStream;

public class CtpExample {
    public static void main(String[] args) {
        ExecutorService executor = Executors.newCachedThreadPool();
        executor.submit(() -> System.out.println(
                IntStream.range(0, 10000000).average().getAsDouble()));
        executor.shutdown();
    }
}
