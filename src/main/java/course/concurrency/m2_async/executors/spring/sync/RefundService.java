package course.concurrency.m2_async.executors.spring.sync;

import org.springframework.stereotype.Component;

@Component
public class RefundService {

    public RefundService(ShopStatistics stat) {
        this.stat = stat;
    }

    private ShopStatistics stat;

    public synchronized void processRefund(long cartId) {
        Long count = getCountFromCart(cartId);
        Long price = getPriceFromCart(cartId);
        // …
        synchronized (stat) {
            Long currentCount = stat.getTotalCount();
            Long currentRevenue = stat.getTotalRevenue();
            stat.reset();
            stat.addData(currentCount - count, price);
        }
    }

    private Long getPriceFromCart(long cartId) {
        if (cartId > 0) {
            return cartId * 20;
        }
        return 10L;
    }

    private Long getCountFromCart(long cartId) {
        if (cartId > 0) {
            return cartId;
        }
        return 1L;
    }
}
