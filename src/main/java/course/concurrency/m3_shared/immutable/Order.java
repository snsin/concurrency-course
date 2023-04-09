package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static course.concurrency.m3_shared.immutable.Order.Status.*;

public class Order {


    public enum Status {NEW, IN_PROGRESS, DELIVERED, INVALID}

    public final static Order INVALID_ORDER = new Order(-1L, List.of(), null, false, INVALID);

    private final long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    public Order(long id, List<Item> items) {
        this(id, Objects.requireNonNull(items), null, false, NEW);
    }

    private Order(long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = items.stream().collect(Collectors.toUnmodifiableList());
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    public boolean checkStatus() {
        return status != INVALID && !items.isEmpty()
               && paymentInfo != null && isPacked;
    }

    public Long getId() {
        return id;
    }

    public List<Item> getItems() {
        return items;
    }

    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    public Order withPaymentInfo(PaymentInfo paymentInfo) {
        if (status == INVALID) {
            return INVALID_ORDER;
        }
        return new Order(this.id, this.items, paymentInfo, this.isPacked, IN_PROGRESS);
    }

    public boolean isPacked() {
        return isPacked;
    }

    public Order pack() {
        if (status == INVALID) {
            return INVALID_ORDER;
        }
        return new Order(this.id, this.items, this.paymentInfo, true, IN_PROGRESS);
    }

    public Status getStatus() {
        return status;
    }

    public Order deliver() {
        if (status == INVALID) {
            return INVALID_ORDER;
        }
        return new Order(this.id, this.items, this.paymentInfo, this.isPacked, DELIVERED);
    }
}
