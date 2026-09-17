package com.library.exceptions;

/**
 * Thrown when attempting to add a book or member that already exists
 * (duplicate ISBN or member ID).
 */
public class DuplicateRecordException extends Exception {
    public DuplicateRecordException(String message) {
        super(message);
    }
}
