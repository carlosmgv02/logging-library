package com.carlosmgv02.logginglibrary.infrastructure.validation;

/**
 * Enumeration representing the connectivity status of Logstash or other external logging systems.
 */
public enum LogstashConnectivity {
    /**
     * Logstash is not configured in the logging system.
     */
    NOT_CONFIGURED,

    /**
     * Logstash is configured and successfully reachable.
     */
    CONNECTED,

    /**
     * Logstash is configured but currently unreachable.
     */
    UNREACHABLE
}