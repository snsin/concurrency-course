package course.concurrency.m3_shared.collections;

import java.util.Set;

public interface RestaurantService {
    Restaurant getByName(String restaurantName);

    void addToStat(String restaurantName);

    Set<String> printStat();
}
