package com.carlosmgv02.logginglibrary.domain.port;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;

public interface LogFilter {
    LogEntry filter(LogEntry logEntry);
    boolean shouldFilter(LogEntry logEntry);
}