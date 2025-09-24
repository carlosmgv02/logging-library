package com.carlosmgv02.logginglibrary.infrastructure.validation;

import lombok.Builder;

import java.util.List;

/**
 * Immutable record representing the results of logging system validation.
 *
 * @param totalAppenders Total number of appenders found in the logging configuration
 * @param startedAppenders Number of appenders that are successfully started
 * @param logstashConnectivity Status of Logstash connectivity
 * @param systemErrors Count of system-level errors detected
 * @param issues List of non-critical issues found during validation
 */
@Builder
public record ValidationResult(
    int totalAppenders,
    int startedAppenders,
    LogstashConnectivity logstashConnectivity,
    long systemErrors,
    List<String> issues
) {
    public ValidationResult {
        if (totalAppenders < 0 || startedAppenders < 0 || systemErrors < 0) {
            throw new IllegalArgumentException("Counts cannot be negative");
        }
        if (startedAppenders > totalAppenders) {
            throw new IllegalArgumentException("Started appenders cannot exceed total appenders");
        }
    }

    /**
     * Checks if the logging system has any critical issues that would prevent proper operation.
     *
     * @return true if there are critical issues, false otherwise
     */
    public boolean hasCriticalIssues() {
        return startedAppenders == 0 || systemErrors > 0;
    }

    /**
     * Checks if the logging system has any warnings that don't prevent operation.
     *
     * @return true if there are warnings, false otherwise
     */
    public boolean hasWarnings() {
        return !issues.isEmpty() || logstashConnectivity == LogstashConnectivity.UNREACHABLE;
    }
}