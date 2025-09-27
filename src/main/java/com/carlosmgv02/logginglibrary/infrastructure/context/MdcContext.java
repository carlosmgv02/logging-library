package com.carlosmgv02.logginglibrary.infrastructure.context;

import org.slf4j.MDC;

import java.util.HashMap;
import java.util.Map;

public final class MdcContext {

    private static final ThreadLocal<Map<String, String>> MDC_BACKUP = new ThreadLocal<>();

    private MdcContext() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Backs up the current MDC context to a ThreadLocal storage
     */
    public static void backup() {
        Map<String, String> currentMdc = MDC.getCopyOfContextMap();
        if (currentMdc != null) {
            MDC_BACKUP.set(new HashMap<>(currentMdc));
        } else {
            MDC_BACKUP.set(new HashMap<>());
        }
    }

    /**
     * Restores the MDC context from ThreadLocal storage
     */
    public static void restore() {
        Map<String, String> backedUpMdc = MDC_BACKUP.get();
        if (backedUpMdc != null && !backedUpMdc.isEmpty()) {
            MDC.setContextMap(backedUpMdc);
        }
    }

    /**
     * Gets the backed up MDC context
     */
    public static Map<String, String> getBackup() {
        Map<String, String> backup = MDC_BACKUP.get();
        return backup != null ? new HashMap<>(backup) : new HashMap<>();
    }

    /**
     * Clears the backup
     */
    public static void clearBackup() {
        MDC_BACKUP.remove();
    }

    /**
     * Ensures MDC has the backed up context, merging if necessary
     */
    public static void ensureContext() {
        Map<String, String> current = MDC.getCopyOfContextMap();
        Map<String, String> backup = MDC_BACKUP.get();

        if (backup != null && !backup.isEmpty()) {
            if (current == null || current.isEmpty()) {
                // No current context, restore from backup
                MDC.setContextMap(backup);
            } else {
                // Merge backup into current, with backup taking precedence for missing keys
                Map<String, String> merged = new HashMap<>(current);
                for (Map.Entry<String, String> entry : backup.entrySet()) {
                    merged.putIfAbsent(entry.getKey(), entry.getValue());
                }
                MDC.setContextMap(merged);
            }
        }
    }
}