package com.ingroup.restaurant.api;

import java.util.List;

public record ApiError(ErrorCode code, String message, List<FieldViolation> errors) {

    public ApiError {
        errors = errors == null ? List.of() : errors;
    }
}
