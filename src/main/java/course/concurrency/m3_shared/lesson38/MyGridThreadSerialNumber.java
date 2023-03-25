package course.concurrency.m3_shared.lesson38;

import java.util.concurrent.atomic.AtomicInteger;

class MyGridThreadSerialNumber {

    private final AtomicInteger nextSerialNum = new AtomicInteger(0);

    private final ThreadLocal<Integer> serialNum = ThreadLocal.withInitial(nextSerialNum::getAndIncrement);

    public int get() {
        return serialNum.get();
    }
}
