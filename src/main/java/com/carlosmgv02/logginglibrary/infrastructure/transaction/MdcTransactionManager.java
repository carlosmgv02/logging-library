package com.carlosmgv02.logginglibrary.infrastructure.transaction;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.Map;

@Component
@Slf4j
public class MdcTransactionManager {

    /**
     * Registers MDC preservation for the current transaction if one is active.
     * This method should be called at the beginning of transactional operations.
     */
    public void preserveMdcInCurrentTransaction() {
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            Map<String, String> mdcContext = MDC.getCopyOfContextMap();

            if (mdcContext != null && !mdcContext.isEmpty()) {
                // Check if we already have an MDC synchronization registered for this transaction
                boolean alreadyRegistered = TransactionSynchronizationManager.getSynchronizations()
                    .stream()
                    .anyMatch(sync -> sync instanceof MdcTransactionSynchronization);

                if (!alreadyRegistered) {
                    MdcTransactionSynchronization mdcSync = new MdcTransactionSynchronization(mdcContext);
                    TransactionSynchronizationManager.registerSynchronization(mdcSync);
                    log.trace("MDC synchronization registered for current transaction");
                }
            }
        }
    }

    /**
     * Ensures MDC context is available in the current thread.
     * This can be used to restore MDC in transaction callbacks.
     */
    public void ensureMdcContext(Map<String, String> mdcContext) {
        if (mdcContext != null && !mdcContext.isEmpty()) {
            // Only set if current MDC is empty or different
            Map<String, String> currentMdc = MDC.getCopyOfContextMap();
            if (currentMdc == null || currentMdc.isEmpty() || !currentMdc.equals(mdcContext)) {
                MDC.setContextMap(mdcContext);
                log.trace("MDC context restored in current thread");
            }
        }
    }
}