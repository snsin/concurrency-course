package course.concurrency.m3_shared.collections;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.LongAdder;

public class RestaurantServiceLongAdder implements RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final Map<String, LongAdder> stat = new ConcurrentHashMap<>();

    @Override
    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    @Override
    public void addToStat(String restaurantName) {
        if (restaurantName == null) {
            return;
        }
        stat.computeIfAbsent(restaurantName, k -> new LongAdder()).increment();
    }

    @Override
    public Set<String> printStat() {
        HashSet<String> result = new HashSet<>();
        stat.forEach((restaurantName, counter) ->
                result.add(String.format("%s - %d", restaurantName, counter.sum()))
        );
        return Collections.unmodifiableSet(result);
    }
}
