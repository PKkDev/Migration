package com.sandbox.core.exceptions.custom;

public class ValidationException extends AppException {

    private final String field;

    public ValidationException(String message, String field) {
        super(message);
        this.field = field;
    }

    public ValidationException(String message, Throwable cause, String field) {
        super(message, cause);
        this.field = field;
    }

    public String getField() {
        return field;
    }
}
