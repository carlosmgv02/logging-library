package com.carlosmgv02.logginglibrary.domain.port;

import com.carlosmgv02.logginglibrary.domain.model.LogEntry;

public interface LogProcessor {
    void process(LogEntry logEntry);
    boolean isEnabled(LogEntry logEntry);
}