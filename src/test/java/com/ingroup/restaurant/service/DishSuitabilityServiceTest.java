package com.ingroup.restaurant.service;

import static org.assertj.core.api.Assertions.assertThat;

import com.ingroup.restaurant.api.Dish;
import com.ingroup.restaurant.config.DishScoreProperties;
import java.time.LocalTime;
import java.util.Map;
import org.junit.jupiter.api.Test;

class DishSuitabilityServiceTest {

    private final DishSuitabilityService service = new DishSuitabilityService(new DishScoreProperties(
            Map.of(Dish.Pancakes, Map.of(7, 10, 9, 10, 10, 4))));

    @Test
    void returnsConfiguredScoreForTheHour() {
        assertThat(service.score(LocalTime.of(7, 0), Dish.Pancakes)).isEqualTo(10);
        assertThat(service.score(LocalTime.of(9, 59), Dish.Pancakes)).isEqualTo(10);
        assertThat(service.score(LocalTime.of(10, 0), Dish.Pancakes)).isEqualTo(4);
    }

    @Test
    void returnsZeroForHourWithoutConfig() {
        assertThat(service.score(LocalTime.of(6, 59), Dish.Pancakes)).isZero();
        assertThat(service.score(LocalTime.of(8, 0), Dish.Pancakes)).isZero();
    }

    @Test
    void returnsZeroForDishWithoutConfig() {
        assertThat(service.score(LocalTime.of(9, 0), Dish.Chicken)).isZero();
    }
}
