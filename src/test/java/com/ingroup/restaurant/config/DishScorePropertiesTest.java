package com.ingroup.restaurant.config;

import com.ingroup.restaurant.domain.Dish;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

class DishScorePropertiesTest {

    private final ApplicationContextRunner runner = new ApplicationContextRunner()
            .withUserConfiguration(Config.class);

    @Test
    void bindsScoresPerDishAndHour() {
        runner.withPropertyValues("restaurant.scores.Pancakes.7=10", "restaurant.scores.Chicken.12=5")
                .run(context -> {
                    DishScoreProperties properties = context.getBean(DishScoreProperties.class);
                    assertThat(properties.scores().get(Dish.Pancakes)).containsEntry(7, 10);
                    assertThat(properties.scores().get(Dish.Chicken)).containsEntry(12, 5);
                });
    }

    @ParameterizedTest
    @ValueSource(strings = {
            "restaurant.scores.Pancakes.7=11",   // score above 10
            "restaurant.scores.Pancakes.7=-1",   // score below 0
            "restaurant.scores.Pancakes.24=5",   // hour above 23
            "restaurant.scores.Pizza.7=5"        // unknown dish
    })
    void invalidConfigFailsStartup(String property) {
        runner.withPropertyValues(property)
                .run(context -> assertThat(context).hasFailed());
    }

    @EnableConfigurationProperties(DishScoreProperties.class)
    static class Config {
    }
}
