package com.ridango.application.exception;

public class SameAccountTransferException extends RuntimeException {
    public SameAccountTransferException() {
        super("Cannot transfer funds to the same account.");
    }
}
