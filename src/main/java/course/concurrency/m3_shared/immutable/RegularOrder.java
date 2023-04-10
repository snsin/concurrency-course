package course.concurrency.m3_shared.immutable;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static course.concurrency.m3_shared.immutable.Order.Status.*;

public final class RegularOrder implements Order {

    private final long id;
    private final List<Item> items;
    private final PaymentInfo paymentInfo;
    private final boolean isPacked;
    private final Status status;

    public RegularOrder(long id, List<Item> items) {
        this(id, Objects.requireNonNull(items), null, false, NEW);
    }

    private RegularOrder(long id, List<Item> items, PaymentInfo paymentInfo, boolean isPacked, Status status) {
        this.id = id;
        this.items = items.stream().collect(Collectors.toUnmodifiableList());
        this.paymentInfo = paymentInfo;
        this.isPacked = isPacked;
        this.status = status;
    }

    @Override
    public boolean checkStatus() {
        return !items.isEmpty() && paymentInfo != null
               && isPacked && status == IN_PROGRESS;
    }

    @Override
    public Long getId() {
        return id;
    }

    @Override
    public List<Item> getItems() {
        return items;
    }

    @Override
    public PaymentInfo getPaymentInfo() {
        return paymentInfo;
    }

    @Override
    public Order withPaymentInfo(PaymentInfo paymentInfo) {
        return new RegularOrder(this.id, this.items, paymentInfo, this.isPacked, IN_PROGRESS);
    }

    @Override
    public boolean isPacked() {
        return isPacked;
    }

    @Override
    public Order packed() {
        if (isPacked) {
            return this;
        }
        return new RegularOrder(this.id, this.items, this.paymentInfo, true, IN_PROGRESS);
    }

    @Override
    public Status getStatus() {
        return status;
    }

    @Override
    public Order delivered() {
        if (this.status == DELIVERED) {
            return this;
        }
        return new RegularOrder(this.id, this.items, this.paymentInfo, this.isPacked, DELIVERED);
    }
}
