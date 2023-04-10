package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class OrderService {

    private final Map<Long, Order> currentOrders = new ConcurrentHashMap<>();
    private final AtomicLong nextIdAtomic = new AtomicLong(0L);

    private long nextId() {
        return nextIdAtomic.getAndIncrement();
    }

    public long createOrder(List<Item> items) {
        long id = nextId();
        Order order = currentOrders.computeIfAbsent(id, key -> new RegularOrder(key, items));
        return order.getId();
    }

    public void updatePaymentInfo(long orderId, PaymentInfo paymentInfo) {
        Order payedOrder = currentOrders.merge(orderId, Order.INVALID_ORDER,
                (old, initial) -> old.withPaymentInfo(paymentInfo));
        if (payedOrder.checkStatus()) {
            deliver(payedOrder);
        }
    }

    public void setPacked(long orderId) {
        Order packedOrder = currentOrders.merge(orderId, Order.INVALID_ORDER,
                (old, initial) -> old.packed());
        if (packedOrder.checkStatus()) {
            deliver(packedOrder);
        }
    }

    private void deliver(Order order) {
        /* ... */
        currentOrders.computeIfPresent(order.getId(), (key, old) -> old.delivered());
    }

    public boolean isDelivered(long orderId) {
        return currentOrders.get(orderId).getStatus() == Order.Status.DELIVERED;
    }

    public Order findOrderById(long id) {
        return currentOrders.get(id);
    }
}
