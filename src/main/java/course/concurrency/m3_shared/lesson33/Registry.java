package course.concurrency.m3_shared.lesson33;

import java.io.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Registry {
    private final Lock lock = new ReentrantLock();

    public static void main(String[] args) {
        var testFile = new File("src/main/java/course/concurrency/m3_shared/lesson33/test.txt");
        var secondTestFile = new File("test.txt");
        var fixedThreadPool = Executors.newFixedThreadPool(2);
        var registry = new Registry();
        Future<?> firstUpdateFuture = fixedThreadPool.submit(() -> registry.updateRegistry(testFile));
        Future<?> secondUpdateFuture = fixedThreadPool.submit(() -> registry.updateRegistry(secondTestFile));

        try {
            firstUpdateFuture.get();
            secondUpdateFuture.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }

        fixedThreadPool.shutdown();
    }

    public void updateRegistry(File file) {
        try (InputStream in = new FileInputStream(file)) {
            lock.lock();
            var str = new String(in.readAllBytes());
            System.out.println(str);
            // ...
        } catch (FileNotFoundException fnf) { /*...*/ } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            lock.unlock();
            /*...*/
        }
    }
}
