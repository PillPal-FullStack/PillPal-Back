package com.project.pillpal.exceptions;

public class UnauthorizedAccessException extends RuntimeException {

    public UnauthorizedAccessException(String resourceType, Long resourceId, Long userId) {
        super(String.format("User %d is not authorized to access %s %d", userId, resourceType, resourceId));
    }

    public UnauthorizedAccessException(String message) {
        super(message);
    }
}
