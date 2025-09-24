package com.carlosmgv02.logginglibrary;

import com.carlosmgv02.logginglibrary.application.service.LoggingApplicationService;
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
}