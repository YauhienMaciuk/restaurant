package com.ingroup.restaurant.service;

import com.ingroup.restaurant.api.Dish;
import java.time.LocalTime;
import org.springframework.stereotype.Service;

@Service
public class DishSuitabilityService {

    public int score(LocalTime time, Dish dish) {
        return 0;
    }
}
