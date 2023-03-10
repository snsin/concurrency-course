package course.concurrency.m3_shared.lesson34;

import java.util.Map;
import java.util.concurrent.*;

public class DeadlockImitationExample {

    public static void main(String[] args) {
        ExecutorService fixedThreadPool = Executors.newFixedThreadPool(2);
        Map<String, Object> concurrentHashMap = new ConcurrentHashMap<>();
        Future<?> computeOne = fixedThreadPool.submit(() ->
        {
            concurrentHashMap.computeIfAbsent("a", key -> getInteger(concurrentHashMap, key, "b"));
        });
        Future<?> computeTwo = fixedThreadPool.submit(() ->
        {
            concurrentHashMap.computeIfAbsent("b", key -> getInteger(concurrentHashMap, key, "a"));
        });
        try {
            computeOne.get();
            computeTwo.get();
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
        fixedThreadPool.shutdown();
        System.out.println(concurrentHashMap);
    }

    private static Integer getInteger(Map<String, Object> map, String key1, String key2) {
        System.out.printf("thread: %s; key: %s\n", Thread.currentThread().getName(), key1);
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        map.computeIfAbsent(key2, (k) -> {
            System.out.println("hi!");
            return 21;
        });
        return Integer.valueOf("42");
    }


}
