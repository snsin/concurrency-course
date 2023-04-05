package course.concurrency.m3_shared.collections;

import java.util.Set;

public interface RestaurantServiceInterface {
    Restaurant getByName(String restaurantName);

    void addToStat(String restaurantName);

    Set<String> printStat();
}
