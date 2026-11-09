package com.ingroup.restaurant.api;

import com.ingroup.restaurant.domain.Dish;
import jakarta.validation.Constraint;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import jakarta.validation.Payload;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * The value must be the label of a {@link Dish}, ignoring case. A null or blank value is left to {@code @NotBlank}.
 */
@Constraint(validatedBy = AllowedDish.Validator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface AllowedDish {

    String message() default "DISH_NOT_ALLOWED";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};

    class Validator implements ConstraintValidator<AllowedDish, String> {

        @Override
        public boolean isValid(String value, ConstraintValidatorContext context) {
            return value == null || value.isBlank() || Dish.fromName(value).isPresent();
        }
    }
}
