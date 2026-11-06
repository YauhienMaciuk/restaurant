package com.ingroup.restaurant.api;

import static org.assertj.core.api.Assertions.assertThat;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import org.junit.jupiter.api.Test;

class ErrorCodeTest {

    @Test
    void everyNameConstantMatchesAnErrorCodeOfTheSameName() {
        Field[] constants = ErrorCode.Names.class.getDeclaredFields();

        assertThat(constants).isNotEmpty();
        Arrays.stream(constants)
                .filter(field -> Modifier.isStatic(field.getModifiers()))
                .forEach(field -> {
                    String value = read(field);
                    assertThat(value).isEqualTo(field.getName());
                    assertThat(ErrorCode.valueOf(value)).isNotNull();
                });
    }

    private static String read(Field field) {
        try {
            return (String) field.get(null);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException(e);
        }
    }
}
