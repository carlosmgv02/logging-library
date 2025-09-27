package com.carlosmgv02.logginglibrary.infrastructure.aop;

import com.carlosmgv02.logginglibrary.infrastructure.context.MdcContext;
import com.carlosmgv02.logginglibrary.infrastructure.transaction.MdcTransactionManager;
import com.carlosmgv02.logginglibrary.shared.constants.LoggingConstants;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.Map;

@Aspect
@Component
@Order(-1000) // Execute before Spring's transaction interceptor (which is typically around 0)
@Slf4j
public class TransactionalMdcInterceptor {

    private final MdcTransactionManager mdcTransactionManager;

    public TransactionalMdcInterceptor(MdcTransactionManager mdcTransactionManager) {
        this.mdcTransactionManager = mdcTransactionManager;
        log.info("🔧 TransactionalMdcInterceptor created and ready to intercept @Transactional methods");
    }

    @Around("@annotation(org.springframework.transaction.annotation.Transactional) || " +
            "@within(org.springframework.transaction.annotation.Transactional)")
    public Object preserveMdcInTransaction(ProceedingJoinPoint joinPoint) throws Throwable {
        // Capture current MDC context before transaction starts
        Map<String, String> mdcContext = MDC.getCopyOfContextMap();

        // Debug logging to understand what's in MDC
        log.info("🎯 AOP Interceptor - Method: {}, MDC Context: {}",
                 joinPoint.getSignature().toShortString(), mdcContext);

        // Note: Backup is already done by HTTP Filter, we just use it

        try {
            // Ensure MDC context is available during transaction execution
            MdcContext.ensureContext();

            // Force restore tracing context if it's missing but available in backup
            Map<String, String> currentMdc = MDC.getCopyOfContextMap();
            Map<String, String> backup = MdcContext.getBackup();

            log.info("🔍 AOP Debug - Current MDC: {}, Backup: {}", currentMdc, backup);

            if (backup != null && backup.containsKey(LoggingConstants.MDC_TRACE_ID)) {
                String backupTraceId = backup.get(LoggingConstants.MDC_TRACE_ID);
                if (backupTraceId != null && (currentMdc == null || !currentMdc.containsKey(LoggingConstants.MDC_TRACE_ID))) {
                    MDC.put(LoggingConstants.MDC_TRACE_ID, backupTraceId);
                    log.info("🔄 Restored missing traceId from backup: {}", backupTraceId);
                }
            }

            if (backup != null && backup.containsKey(LoggingConstants.MDC_SPAN_ID)) {
                String backupSpanId = backup.get(LoggingConstants.MDC_SPAN_ID);
                if (backupSpanId != null && (currentMdc == null || !currentMdc.containsKey(LoggingConstants.MDC_SPAN_ID))) {
                    MDC.put(LoggingConstants.MDC_SPAN_ID, backupSpanId);
                    log.debug("Restored missing spanId from backup: {}", backupSpanId);
                }
            }

            // Also register with transaction manager for transaction callbacks
            if (mdcContext != null) {
                mdcTransactionManager.ensureMdcContext(mdcContext);
                mdcTransactionManager.preserveMdcInCurrentTransaction();
                log.debug("MDC context preserved for transactional method: {}, context size: {}",
                         joinPoint.getSignature().toShortString(), mdcContext.size());
            } else {
                log.warn("No MDC context available for transactional method: {}",
                        joinPoint.getSignature().toShortString());
            }

            return joinPoint.proceed();

        } finally {
            // Always restore MDC context after transaction (but keep backup for other transactions)
            MdcContext.restore();
            log.debug("MDC context restored after transaction for method: {}",
                     joinPoint.getSignature().toShortString());
        }
    }
}