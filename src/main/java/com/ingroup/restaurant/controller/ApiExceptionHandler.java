package com.ingroup.restaurant.controller;

import com.fasterxml.jackson.databind.JsonMappingException.Reference;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.ingroup.restaurant.api.ApiError;
import com.ingroup.restaurant.api.Dish;
import com.ingroup.restaurant.api.ErrorCode;
import com.ingroup.restaurant.api.FieldViolation;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final String ALLOWED_DISHES =
            Arrays.stream(Dish.values()).map(Enum::name).collect(Collectors.joining(", "));

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiError> handleValidation(MethodArgumentNotValidException ex) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> violation(error.getField(), ErrorCode.valueOf(error.getDefaultMessage())))
                .sorted(Comparator.comparing(FieldViolation::field).thenComparing(FieldViolation::code))
                .toList();
        return badRequest(new ApiError(ErrorCode.VALIDATION_FAILED, "Request validation failed", violations));
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiError> handleUnreadable(HttpMessageNotReadableException ex) {
        if (ex.getCause() instanceof InvalidFormatException cause && cause.getTargetType() == Dish.class) {
            String field = cause.getPath().stream().map(Reference::getFieldName).reduce((a, b) -> b).orElse("dish");
            FieldViolation violation = new FieldViolation(field, ErrorCode.DISH_NOT_ALLOWED,
                    "dish '" + cause.getValue() + "' is not allowed. Allowed dishes: " + ALLOWED_DISHES);
            return badRequest(new ApiError(ErrorCode.VALIDATION_FAILED, "Request validation failed", List.of(violation)));
        }
        return badRequest(new ApiError(ErrorCode.MALFORMED_REQUEST, "Request body is missing or is not valid JSON", null));
    }

    private static FieldViolation violation(String field, ErrorCode code) {
        return new FieldViolation(field, code, describe(code));
    }

    private static String describe(ErrorCode code) {
        return switch (code) {
            case TIME_REQUIRED -> "time is required, expected format HH:mm";
            case INVALID_TIME_FORMAT -> "time must be a valid time in HH:mm format";
            case DISH_REQUIRED -> "dish is required. Allowed dishes: " + ALLOWED_DISHES;
            default -> throw new IllegalArgumentException("No field message for " + code);
        };
    }

    private static ResponseEntity<ApiError> badRequest(ApiError body) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(body);
    }
}
