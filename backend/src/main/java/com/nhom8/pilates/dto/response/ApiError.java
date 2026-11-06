package com.nhom8.pilates.dto.response;

import java.time.Instant;
import java.util.Map;

public class ApiError {
    private final boolean success = false;
    private final String code;
    private final String message;
    private final Map<String, String> fieldErrors;
    private final String path;
    private final Instant timestamp = Instant.now();

    public ApiError(String code, String message, Map<String, String> fieldErrors, String path) {
        this.code = code;
        this.message = message;
        this.fieldErrors = fieldErrors;
        this.path = path;
    }

    public boolean isSuccess() { return success; }
    public String getCode() { return code; }
    public String getMessage() { return message; }
    public Map<String, String> getFieldErrors() { return fieldErrors; }
    public String getPath() { return path; }
    public Instant getTimestamp() { return timestamp; }
}
