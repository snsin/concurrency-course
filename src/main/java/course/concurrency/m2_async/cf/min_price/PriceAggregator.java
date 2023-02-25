package course.concurrency.m2_async.cf.min_price;

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

public class PriceAggregator {

    private static final long timeoutMs = 2_980L;
    private PriceRetriever priceRetriever = new PriceRetriever();

    public void setPriceRetriever(PriceRetriever priceRetriever) {
        this.priceRetriever = priceRetriever;
    }

    private Collection<Long> shopIds = Set.of(10L, 45L, 66L, 345L, 234L, 333L, 67L, 123L, 768L);

    public void setShops(Collection<Long> shopIds) {
        this.shopIds = shopIds;
    }

    public double getMinPrice(long itemId) {
        @SuppressWarnings("unchecked")
        CompletableFuture<Double>[] completableFutures = shopIds.stream()
                .map(shopId -> CompletableFuture
                        .supplyAsync(() -> priceRetriever.getPrice(itemId, shopId))
                        .completeOnTimeout(null, timeoutMs, TimeUnit.MILLISECONDS)
                        .exceptionally(e -> null))
                .toArray(CompletableFuture[]::new);
        return CompletableFuture.allOf(completableFutures)
                .thenCompose(voidIgnored -> {
                    Double minPrice = Arrays.stream(completableFutures)
                            .map(CompletableFuture::join)
                            .filter(Objects::nonNull)
                            .min(Double::compareTo)
                            .orElse(Double.NaN);
                    return CompletableFuture.completedFuture(minPrice);
                }).join();
    }
}
