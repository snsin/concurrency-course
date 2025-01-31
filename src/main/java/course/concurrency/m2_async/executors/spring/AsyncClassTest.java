package course.concurrency.m2_async.executors.spring;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.stereotype.Component;

@Component("asyncClass")
@EnableAsync
public class AsyncClassTest {

    @Autowired
    public ApplicationContext context;

    @Autowired
    @Qualifier("applicationTaskExecutor")
    private ThreadPoolTaskExecutor executor;

    @Async("applicationTaskExecutor")
    public void runAsyncTask() {
        var currentThread = Thread.currentThread();
        System.out.println("runAsyncTask: " + currentThread.getName() + " id: " + currentThread.getId());
        var asyncClassBean = context.getBean("asyncClass", AsyncClassTest.class);
        asyncClassBean.internalTask();
    }

    @Async("applicationTaskExecutor")
    public void internalTask() {
        var currentThread = Thread.currentThread();
        System.out.println("internalTask: " + currentThread.getName() + " id: " + Thread.currentThread().getId());
    }
}
