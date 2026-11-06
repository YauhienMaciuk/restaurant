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

    private Set<ConstraintViolation<DishSuitabilityRequest>> validate(String time, String dish) {
        return validator.validate(new DishSuitabilityRequest(time, dish));
    }

    @ParameterizedTest
    @ValueSource(strings = {"00:00", "09:30", "12:59", "23:59"})
    void validTimeHasNoViolations(String time) {
        assertThat(validate(time, "Pancakes")).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"9:30", "24:00", "12:60", "12-30", "noon", ""})
    void invalidTimeIsRejected(String time) {
        assertThat(validate(time, "Pancakes"))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("INVALID_TIME_FORMAT");
    }

    @Test
    void missingTimeAndDishAreRejected() {
        assertThat(validate(null, null))
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("TIME_REQUIRED", "DISH_REQUIRED");
    }

    @ParameterizedTest
    @ValueSource(strings = {"Pancakes", "chicken", "ICECREAM", " IceCream "})
    void validDishHasNoViolations(String dish) {
        assertThat(validate("09:30", dish)).isEmpty();
    }

    @ParameterizedTest
    @ValueSource(strings = {"Pizza", "PancakeS1"})
    void unknownDishIsRejected(String dish) {
        assertThat(validate("09:30", dish))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("DISH_NOT_ALLOWED");
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   "})
    void blankDishIsRequired(String dish) {
        assertThat(validate("09:30", dish))
                .extracting(ConstraintViolation::getMessage)
                .containsExactly("DISH_REQUIRED");
    }

    @Test
    void invalidTimeAndDishAreBothReported() {
        assertThat(validate("99:39", "PancakeS1"))
                .extracting(ConstraintViolation::getMessage)
                .containsExactlyInAnyOrder("INVALID_TIME_FORMAT", "DISH_NOT_ALLOWED");
    }
}
