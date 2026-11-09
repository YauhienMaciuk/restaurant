package com.ingroup.restaurant.api;

public record FieldViolation(String field, ErrorCode code, String message) {
}
