package com.ingroup.restaurant.service;

import com.ingroup.restaurant.config.DishScoreProperties;
import com.ingroup.restaurant.domain.Dish;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class DishSuitabilityServiceTest {

    private final DishSuitabilityService service = new DishSuitabilityService(new DishScoreProperties(Map.of(
            Dish.Pancakes, Map.of(0, 3, 7, 10, 9, 8, 23, 1),
            Dish.Chicken, Map.of(7, 2))));

    @ParameterizedTest
    @CsvSource({"00:00,3", "07:00,10", "09:00,8", "23:00,1"})
    void returnsConfiguredScoreForTheHour(String time, int expected) {
        assertThat(service.score(LocalTime.parse(time), Dish.Pancakes)).isEqualTo(expected);
    }

    @ParameterizedTest
    @CsvSource({"07:00", "07:30", "07:59"})
    void minutesDoNotChangeTheScore(String time) {
        assertThat(service.score(LocalTime.parse(time), Dish.Pancakes)).isEqualTo(10);
    }

    @ParameterizedTest
    @CsvSource({"06:59", "08:00", "22:59"})
    void returnsZeroForHourWithoutConfig(String time) {
        assertThat(service.score(LocalTime.parse(time), Dish.Pancakes)).isZero();
    }

    @Test
    void scoresAreIndependentPerDish() {
        LocalTime time = LocalTime.of(7, 0);

        assertThat(service.score(time, Dish.Pancakes)).isEqualTo(10);
        assertThat(service.score(time, Dish.Chicken)).isEqualTo(2);
    }

    @Test
    void returnsZeroForDishWithoutConfig() {
        assertThat(service.score(LocalTime.of(7, 0), Dish.IceCream)).isZero();
    }

    @Test
    void returnsZeroWhenNothingIsConfigured() {
        DishSuitabilityService empty = new DishSuitabilityService(new DishScoreProperties(null));

        assertThat(empty.score(LocalTime.of(7, 0), Dish.Pancakes)).isZero();
    }
}
