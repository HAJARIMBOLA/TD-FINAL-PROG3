package com.agri.federation.exception;

public class ConflictException extends RuntimeException {
    private final String conflictingField;

    public ConflictException(String message, String conflictingField) {
        super(message);
        this.conflictingField = conflictingField;
    }

    public String getConflictingField() {
        return conflictingField;
    }
}