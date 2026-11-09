package com.ingroup.restaurant.api;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DishSuitabilityRequest(
        @Schema(description = "Time of day, 24h HH:mm", example = "09:30")
        @NotNull(message = "TIME_REQUIRED")
        @Pattern(regexp = "([01]\\d|2[0-3]):[0-5]\\d", message = "INVALID_TIME_FORMAT")
        String time,
        @Schema(description = "Pancakes, Chicken or IceCream (case-insensitive)", example = "Pancakes")
        @NotBlank(message = "DISH_REQUIRED")
        @AllowedDish
        String dish) {
}
