package course.concurrency.m3_shared.immutable;

import java.util.List;

public interface Order {
    Order INVALID_ORDER = new Order() {
        @Override
        public boolean checkStatus() {
            return false;
        }

        @Override
        public Long getId() {
            return -1L;
        }

        @Override
        public List<Item> getItems() {
            return List.of();
        }

        @Override
        public PaymentInfo getPaymentInfo() {
            return null;
        }

        @Override
        public Order withPaymentInfo(PaymentInfo paymentInfo) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean isPacked() {
            return false;
        }

        @Override
        public Order packed() {
            throw new UnsupportedOperationException();
        }

        @Override
        public Status getStatus() {
            return null;
        }

        @Override
        public Order delivered() {
            throw new UnsupportedOperationException();
        }
    };

    boolean checkStatus();

    Long getId();

    List<Item> getItems();

    PaymentInfo getPaymentInfo();

    Order withPaymentInfo(PaymentInfo paymentInfo);

    boolean isPacked();

    Order packed();

    Status getStatus();

    Order delivered();

    enum Status {NEW, IN_PROGRESS, DELIVERED}
}
