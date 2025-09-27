package com.carlosmgv02.logginglibrary.infrastructure.transaction;

import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.transaction.support.TransactionSynchronization;

import java.util.Map;

@Slf4j
public class MdcTransactionSynchronization implements TransactionSynchronization {

    private final Map<String, String> mdcContext;

    public MdcTransactionSynchronization(Map<String, String> mdcContext) {
        this.mdcContext = mdcContext;
    }

    @Override
    public void suspend() {
        // Transaction is being suspended, clear MDC to avoid pollution
        log.trace("Transaction suspended, clearing MDC");
        MDC.clear();
    }

    @Override
    public void resume() {
        // Transaction is being resumed, restore MDC context
        if (mdcContext != null && !mdcContext.isEmpty()) {
            MDC.setContextMap(mdcContext);
            log.trace("Transaction resumed, MDC context restored");
        }
    }

    @Override
    public void beforeCommit(boolean readOnly) {
        // Ensure MDC is available before commit
        if (mdcContext != null && !mdcContext.isEmpty()) {
            MDC.setContextMap(mdcContext);
        }
    }

    @Override
    public void beforeCompletion() {
        // Ensure MDC is available before completion
        if (mdcContext != null && !mdcContext.isEmpty()) {
            MDC.setContextMap(mdcContext);
        }
    }

    @Override
    public void afterCommit() {
        // Restore MDC after commit
        if (mdcContext != null && !mdcContext.isEmpty()) {
            MDC.setContextMap(mdcContext);
            log.trace("Transaction committed, MDC context maintained");
        }
    }

    @Override
    public void afterCompletion(int status) {
        // Restore MDC after transaction completion
        if (mdcContext != null && !mdcContext.isEmpty()) {
            MDC.setContextMap(mdcContext);
            log.trace("Transaction completed with status {}, MDC context restored", status);
        }
    }
}