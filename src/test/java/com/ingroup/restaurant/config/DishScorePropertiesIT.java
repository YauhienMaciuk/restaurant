package com.ingroup.restaurant.config;

import com.ingroup.restaurant.domain.Dish;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.EnumSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class DishScorePropertiesIT {

    @Autowired
    private DishScoreProperties dishScoreProperties;

    @ParameterizedTest
    @EnumSource(Dish.class)
    void everyDishHasAScoreForEveryHour(Dish dish) {
        assertThat(dishScoreProperties.scores().get(dish)).containsOnlyKeys(0, 1, 2, 3, 4, 5, 6, 7, 8, 9, 10, 11, 12, 13,
                14, 15, 16, 17, 18, 19, 20, 21, 22, 23);
    }
}
