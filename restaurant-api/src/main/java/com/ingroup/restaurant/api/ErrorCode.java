package com.ingroup.restaurant.api;

/**
 * Stable, machine-readable error codes. Clients should rely on these, not on message text.
 */
public enum ErrorCode {
    MALFORMED_REQUEST,
    VALIDATION_FAILED,
    TIME_REQUIRED,
    INVALID_TIME_FORMAT,
    DISH_REQUIRED,
    DISH_NOT_ALLOWED;

    /** Compile-time constants of the codes, for use as validation annotation messages. */
    public static final class Names {
        public static final String TIME_REQUIRED = "TIME_REQUIRED";
        public static final String INVALID_TIME_FORMAT = "INVALID_TIME_FORMAT";
        public static final String DISH_REQUIRED = "DISH_REQUIRED";
        public static final String DISH_NOT_ALLOWED = "DISH_NOT_ALLOWED";

        private Names() {
        }
    }
}
