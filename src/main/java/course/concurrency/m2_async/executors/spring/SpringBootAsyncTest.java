package course.concurrency.m2_async.executors.spring;

import course.concurrency.m2_async.executors.spring.sync.RefundService;
import course.concurrency.m2_async.executors.spring.sync.ShopStatistics;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class SpringBootAsyncTest {

    @Autowired
    private AsyncClassTest testClass;

    @Autowired
    private ShopStatistics statistics;

    @Autowired
    private RefundService refundService;

    // this method executes after application start
    @EventListener(ApplicationReadyEvent.class)
    public void actionAfterStartup() {
        testClass.runAsyncTask();
        statistics.addData(2L, 20L);
        refundService.processRefund(1L);
    }

    public static void main(String[] args) {
        SpringApplication.run(SpringBootAsyncTest.class, args);
    }
}
