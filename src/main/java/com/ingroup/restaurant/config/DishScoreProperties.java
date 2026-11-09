package com.ingroup.restaurant.config;

import com.ingroup.restaurant.domain.Dish;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.validation.annotation.Validated;

import java.util.Map;

@Validated
@ConfigurationProperties("restaurant")
public record DishScoreProperties(Map<Dish, Map<@Min(0) @Max(23) Integer, @Min(0) @Max(10) Integer>> scores) {

    public DishScoreProperties {
        scores = scores == null ? Map.of() : scores;
    }
}
