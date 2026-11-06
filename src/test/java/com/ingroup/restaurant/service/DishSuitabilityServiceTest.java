package com.ingroup.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ingroup.restaurant.api.Dish;
import java.time.LocalTime;
import org.junit.jupiter.api.Test;

class DishSuitabilityServiceTest {

    private final DishSuitabilityService service = new DishSuitabilityService();

    @Test
    void scoreIsZeroForNow() {
        assertThat(service.score(LocalTime.of(9, 30), Dish.Pancakes)).isZero();
    }
}
