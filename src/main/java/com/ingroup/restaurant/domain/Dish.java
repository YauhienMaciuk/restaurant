package com.ingroup.restaurant.domain;

import java.util.Arrays;
import java.util.Optional;

public enum Dish {
    PANCAKES("Pancakes"),
    CHICKEN("Chicken"),
    ICE_CREAM("IceCream");

    private final String label;

    Dish(String label) {
        this.label = label;
    }

    public String label() {
        return label;
    }

    /**
     * Lookup by label, ignoring case and surrounding whitespace.
     */
    public static Optional<Dish> fromName(String name) {
        if (name == null) {
            return Optional.empty();
        }
        String stripped = name.strip();
        return Arrays.stream(values())
                .filter(dish -> dish.label.equalsIgnoreCase(stripped))
                .findFirst();
    }
}
