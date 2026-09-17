package com.library.exceptions;

/**
 * Thrown when a member attempts to borrow a book that has no available copies,
 * or that does not exist in the catalogue.
 */
public class BookNotAvailableException extends Exception {
    public BookNotAvailableException(String message) {
        super(message);
    }
}
