package com.teample.packages.exception;

public class EmptyDataException extends RuntimeException {

    public EmptyDataException() {
        super();
    }

    public EmptyDataException(String message) {
        super(message);
    }

    public EmptyDataException(String message, Throwable cause) {
        super(message, cause);
    }

    public EmptyDataException(Throwable cause) {
        super(cause);
    }
}

