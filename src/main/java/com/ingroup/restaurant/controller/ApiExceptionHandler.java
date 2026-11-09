package com.ingroup.restaurant.controller;

import com.ingroup.restaurant.api.ApiError;
import com.ingroup.restaurant.api.ErrorCode;
import com.ingroup.restaurant.api.FieldViolation;
import com.ingroup.restaurant.domain.Dish;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ApiExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(ApiExceptionHandler.class);

    private static final String ALLOWED_DISHES = Arrays.stream(Dish.values())
            .map(Dish::label)
            .collect(Collectors.joining(", "));

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleValidation(MethodArgumentNotValidException ex) {
        List<FieldViolation> violations = ex.getBindingResult().getFieldErrors().stream()
                .map(ApiExceptionHandler::toViolation)
                .sorted(Comparator.comparing(FieldViolation::field).thenComparing(FieldViolation::code))
                .toList();
        log.warn("Rejected request, validation failed: {}", violations.stream()
                .map(v -> v.field() + "=" + v.code())
                .toList());

        return new ApiError(ErrorCode.VALIDATION_FAILED, "Request validation failed", violations);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ApiError handleUnreadable(HttpMessageNotReadableException ex) {
        log.warn("Rejected request, body is missing or not valid JSON");

        return new ApiError(ErrorCode.MALFORMED_REQUEST, "Request body is missing or is not valid JSON", null);
    }

    private static FieldViolation toViolation(FieldError error) {
        ErrorCode code = ErrorCode.fromName(error.getDefaultMessage()).orElse(ErrorCode.INVALID_VALUE);
        return new FieldViolation(error.getField(), code, messageFor(code, error));
    }

    private static String messageFor(ErrorCode code, FieldError error) {
        return switch (code) {
            case TIME_REQUIRED -> "time is required, expected format HH:mm";
            case INVALID_TIME_FORMAT -> "time must be a valid time in HH:mm format";
            case DISH_NOT_ALLOWED ->
                    "dish '" + error.getRejectedValue() + "' is not allowed. Allowed dishes: " + ALLOWED_DISHES;
            case DISH_REQUIRED -> "dish is required. Allowed dishes: " + ALLOWED_DISHES;
            default -> error.getField() + " " + error.getDefaultMessage();
        };
    }
}
