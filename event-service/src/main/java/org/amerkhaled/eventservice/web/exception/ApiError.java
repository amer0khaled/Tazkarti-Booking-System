package org.amerkhaled.eventservice.web.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
        int status,
        String message,
        LocalDateTime timestamp,
        Map<String, String> details // optional, for validation errors
) {
    public ApiError(int status, String message, LocalDateTime timestamp) {
        this(status, message, timestamp, null);
    }
}
