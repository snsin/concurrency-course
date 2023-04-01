package course.concurrency.m3_shared.collections;

import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final Map<String, Long> stat = new ConcurrentHashMap<>();

    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    public void addToStat(String restaurantName) {
        if (restaurantName == null) {
            return;
        }
        stat.compute(restaurantName,
                (key, counter) -> counter == null ? 1L : counter + 1);
    }

    public Set<String> printStat() {
        HashSet<String> result = new HashSet<>();
        stat.forEach((restaurantName, counter) ->
                result.add(String.format("%s - %d", restaurantName, counter)));
        return Collections.unmodifiableSet(result);
    }
}
