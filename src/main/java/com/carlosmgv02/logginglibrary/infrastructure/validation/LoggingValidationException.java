package com.carlosmgv02.logginglibrary.infrastructure.validation;

/**
 * Exception thrown when logging system validation fails.
 *
 * <p>This exception is used to indicate specific failures in the logging system
 * that would prevent proper operation, such as:
 * <ul>
 *   <li>No appenders started</li>
 *   <li>Critical configuration errors</li>
 *   <li>External system connectivity failures (when configured to fail)</li>
 * </ul>
 */
public class LoggingValidationException extends RuntimeException {

    public LoggingValidationException(String message) {
        super(message);
    }

}