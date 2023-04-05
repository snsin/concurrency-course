package course.concurrency.m3_shared.collections;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class RestaurantServiceLong implements RestaurantService {

    private final Map<String, Restaurant> restaurantMap = new ConcurrentHashMap<>() {{
        put("A", new Restaurant("A"));
        put("B", new Restaurant("B"));
        put("C", new Restaurant("C"));
    }};

    private final Map<String, Long> stat = new ConcurrentHashMap<>();

    @Override
    public Restaurant getByName(String restaurantName) {
        addToStat(restaurantName);
        return restaurantMap.get(restaurantName);
    }

    @Override
    public void addToStat(String restaurantName) {
        stat.merge(restaurantName, 1L, Long::sum);
    }

    @Override
    public Set<String> printStat() {
        Set<String> result = new HashSet<>();
        stat.forEach((key, count) ->
                result.add(String.format("%s - %d", key, count)));
        return result;
    }
}
