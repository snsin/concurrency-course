package course.concurrency.m2_async.executors.spring;

import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Component
public class ExternalAsyncClass {

    @Async
    public void externalTask() {
        var currentThread = Thread.currentThread();
        System.out.println("external task: " + currentThread.getName() + " id: " + currentThread.getId());
    }
}
