package com.ingroup.restaurant.service;

import com.ingroup.restaurant.api.Dish;
import com.ingroup.restaurant.config.DishScoreProperties;
import java.time.LocalTime;
import java.util.Map;
import org.springframework.stereotype.Service;

@Service
public class DishSuitabilityService {

    private final DishScoreProperties properties;

    public DishSuitabilityService(DishScoreProperties properties) {
        this.properties = properties;
    }

    /** Returns the configured score for the dish at the hour of the given time, or 0 if not configured. */
    public int score(LocalTime time, Dish dish) {
        return properties.scores().getOrDefault(dish, Map.of()).getOrDefault(time.getHour(), 0);
    }
}
