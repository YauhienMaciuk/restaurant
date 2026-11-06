package com.ingroup.restaurant.api;

import java.util.Arrays;
import java.util.Optional;

public enum Dish {
    Pancakes,
    Chicken,
    IceCream;

    /** Lookup by name, ignoring case and surrounding whitespace. */
    public static Optional<Dish> parse(String value) {
        if (value == null) {
            return Optional.empty();
        }
        String name = value.strip();
        return Arrays.stream(values()).filter(dish -> dish.name().equalsIgnoreCase(name)).findFirst();
    }
}
