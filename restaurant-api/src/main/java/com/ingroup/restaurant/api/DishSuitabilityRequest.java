package com.ingroup.restaurant.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;

public record DishSuitabilityRequest(
        @NotNull(message = ErrorCode.Names.TIME_REQUIRED)
        @Pattern(regexp = "([01]\\d|2[0-3]):[0-5]\\d", message = ErrorCode.Names.INVALID_TIME_FORMAT)
        String time,
        @NotBlank(message = ErrorCode.Names.DISH_REQUIRED)
        @AllowedDish
        String dish) {
}
