package com.carlosmgv02.logginglibrary;

import com.carlosmgv02.logginglibrary.application.service.LoggingApplicationService;
import com.carlosmgv02.logginglibrary.shared.util.MessageFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CustomLogger {

    private static LoggingApplicationService loggingService;

    @Autowired
    public void setLoggingService(LoggingApplicationService loggingService) {
        CustomLogger.loggingService = loggingService;
    }

    public static void trace(String message) {
        if (loggingService != null) {
            loggingService.trace(message);
        }
    }

    public static void trace(String message, Throwable throwable) {
        if (loggingService != null) {
            loggingService.trace(message, throwable);
        }
    }

    public static void debug(String message) {
        if (loggingService != null) {
            loggingService.debug(message);
        }
    }

    public static void debug(String message, Throwable throwable) {
        if (loggingService != null) {
            loggingService.debug(message, throwable);
        }
    }

    public static void info(String message) {
        if (loggingService != null) {
            loggingService.info(message);
        }
    }

    public static void info(String message, Throwable throwable) {
        if (loggingService != null) {
            loggingService.info(message, throwable);
        }
    }

    public static void warn(String message) {
        if (loggingService != null) {
            loggingService.warn(message);
        }
    }

    public static void warn(String message, Throwable throwable) {
        if (loggingService != null) {
            loggingService.warn(message, throwable);
        }
    }

    public static void error(String message) {
        if (loggingService != null) {
            loggingService.error(message);
        }
    }

    public static void error(String message, Throwable throwable) {
        if (loggingService != null) {
            loggingService.error(message, throwable);
        }
    }

    // Formatted logging methods with placeholder support

    public static void trace(String messagePattern, Object... arguments) {
        if (loggingService != null) {
            MessageFormatter.FormattedMessage formatted = MessageFormatter.formatWithThrowable(messagePattern, arguments);
            if (formatted.hasThrowable()) {
                loggingService.trace(formatted.getMessage(), formatted.getThrowable());
            } else {
                loggingService.trace(formatted.getMessage());
            }
        }
    }

    public static void debug(String messagePattern, Object... arguments) {
        if (loggingService != null) {
            MessageFormatter.FormattedMessage formatted = MessageFormatter.formatWithThrowable(messagePattern, arguments);
            if (formatted.hasThrowable()) {
                loggingService.debug(formatted.getMessage(), formatted.getThrowable());
            } else {
                loggingService.debug(formatted.getMessage());
            }
        }
    }

    public static void info(String messagePattern, Object... arguments) {
        if (loggingService != null) {
            MessageFormatter.FormattedMessage formatted = MessageFormatter.formatWithThrowable(messagePattern, arguments);
            if (formatted.hasThrowable()) {
                loggingService.info(formatted.getMessage(), formatted.getThrowable());
            } else {
                loggingService.info(formatted.getMessage());
            }
        }
    }

    public static void warn(String messagePattern, Object... arguments) {
        if (loggingService != null) {
            MessageFormatter.FormattedMessage formatted = MessageFormatter.formatWithThrowable(messagePattern, arguments);
            if (formatted.hasThrowable()) {
                loggingService.warn(formatted.getMessage(), formatted.getThrowable());
            } else {
                loggingService.warn(formatted.getMessage());
            }
        }
    }

    public static void error(String messagePattern, Object... arguments) {
        if (loggingService != null) {
            MessageFormatter.FormattedMessage formatted = MessageFormatter.formatWithThrowable(messagePattern, arguments);
            if (formatted.hasThrowable()) {
                loggingService.error(formatted.getMessage(), formatted.getThrowable());
            } else {
                loggingService.error(formatted.getMessage());
            }
        }
    }
}