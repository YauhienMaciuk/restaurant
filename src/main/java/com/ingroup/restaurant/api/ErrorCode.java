package com.ingroup.restaurant.api;

import java.util.Arrays;
import java.util.Optional;

public enum ErrorCode {
    MALFORMED_REQUEST,
    VALIDATION_FAILED,

    TIME_REQUIRED,
    INVALID_TIME_FORMAT,
    DISH_REQUIRED,
    DISH_NOT_ALLOWED,
    INVALID_VALUE;

    public static Optional<ErrorCode> fromName(String name) {
        return Arrays.stream(values())
                .filter(code -> code.name().equals(name))
                .findFirst();
    }
}
