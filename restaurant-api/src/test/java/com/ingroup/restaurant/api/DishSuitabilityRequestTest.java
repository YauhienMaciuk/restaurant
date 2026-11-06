package com.ingroup.restaurant.api;

import static org.assertj.core.api.Assertions.assertThat;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import jakarta.validation.ValidatorFactory;
import java.util.Set;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

class DishSuitabilityRequestTest {

    private static ValidatorFactory validatorFactory;
    private static Validator validator;

    @BeforeAll
    static void createValidator() {
        validatorFactory = Validation.buildDefaultValidatorFactory();
        validator = validatorFactory.getValidator();
    }

    @AfterAll
    static void closeValidatorFactory() {
        validatorFactory.close();
    }

    private Set<ConstraintViolation<DishSuitabilityRequest>> validate(String time, Dish dish) {
        return validator.validate(new DishSuitabilityRequest(time, dish));
    }

    @ParameterizedTest
    @ValueSource(strings = {"00:00", "09:30", "12:59", "23:59"})
    void validTimeHasNoViolations(String time) {
        assertThat(validate(time, Dish.Pancakes)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"9:30", "24:00", "12:60", "12-30", "noon", ""})
    void invalidTimeIsRejected(String time) {
        assertThat(validate(time, Dish.Pancakes))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("INVALID_TIME_FORMAT");
    }

    @Test
    void missingTimeAndDishAreRejected() {
        assertThat(validate(null, null))
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("TIME_REQUIRED", "DISH_REQUIRED");
    }
}
