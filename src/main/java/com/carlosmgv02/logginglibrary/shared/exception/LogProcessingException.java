package com.carlosmgv02.logginglibrary.shared.exception;

public class LogProcessingException extends LoggingException {
    public LogProcessingException(String message) {
        super(message);
    }

    public LogProcessingException(String message, Throwable cause) {
        super(message, cause);
    }
}