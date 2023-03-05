package course.concurrency.m3_shared.lesson33;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockAndSynchronizedExample {
    private Integer value = 0;
    private Lock lock = new ReentrantLock();

    public synchronized void add(Integer newValue) {
        this.value += newValue;
    }

    public Integer getValue() {
        try {
            lock.lock();
            log("Current value is " + value);
            return value;
        } finally {
            lock.unlock();
        }
    }

    private void log(String s) {
        System.out.println(s);
    }
}
