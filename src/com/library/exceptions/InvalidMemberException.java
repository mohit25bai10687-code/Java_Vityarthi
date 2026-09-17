package com.library.exceptions;

/**
 * Thrown when an operation references a member that does not exist,
 * or when a member is not eligible to borrow (limit reached / unpaid fine).
 */
public class InvalidMemberException extends Exception {
    public InvalidMemberException(String message) {
        super(message);
    }
}
