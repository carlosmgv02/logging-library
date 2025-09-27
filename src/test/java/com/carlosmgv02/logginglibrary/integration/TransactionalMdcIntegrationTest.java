package com.carlosmgv02.logginglibrary.integration;

import com.carlosmgv02.logginglibrary.shared.constants.LoggingConstants;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.MDC;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.stereotype.Service;
import org.springframework.test.context.TestPropertySource;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@TestPropertySource(properties = {
    "logging.transaction.mdc.enabled=true"
})
class TransactionalMdcIntegrationTest {

    @BeforeEach
    void setUp() {
        MDC.clear();
    }

    @Test
    void shouldPreserveMdcInTransactionalMethod() {
        // Given: Set up initial MDC context
        String expectedTraceId = "test-trace-123";
        String expectedSpanId = "test-span-456";

        MDC.put(LoggingConstants.MDC_TRACE_ID, expectedTraceId);
        MDC.put(LoggingConstants.MDC_SPAN_ID, expectedSpanId);

        // When: Call transactional method
        TestTransactionalService service = new TestTransactionalService();
        String[] result = service.performTransactionalOperation();

        // Then: MDC should be preserved during transaction
        assertNotNull(result);
        assertEquals(expectedTraceId, result[0]);
        assertEquals(expectedSpanId, result[1]);

        // And: MDC should still be available after transaction
        assertEquals(expectedTraceId, MDC.get(LoggingConstants.MDC_TRACE_ID));
        assertEquals(expectedSpanId, MDC.get(LoggingConstants.MDC_SPAN_ID));
    }

    @Test
    void shouldPreserveMdcInNestedTransactions() {
        // Given: Set up initial MDC context
        String expectedTraceId = "nested-trace-789";
        MDC.put(LoggingConstants.MDC_TRACE_ID, expectedTraceId);

        // When: Call nested transactional methods
        TestTransactionalService service = new TestTransactionalService();
        String result = service.performNestedTransactionalOperation();

        // Then: MDC should be preserved through all nested transactions
        assertEquals(expectedTraceId, result);
        assertEquals(expectedTraceId, MDC.get(LoggingConstants.MDC_TRACE_ID));
    }

    @Service
    @Transactional
    static class TestTransactionalService {

        public String[] performTransactionalOperation() {
            // Capture MDC values during transaction
            String traceId = MDC.get(LoggingConstants.MDC_TRACE_ID);
            String spanId = MDC.get(LoggingConstants.MDC_SPAN_ID);

            // Simulate some work
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }

            return new String[]{traceId, spanId};
        }

        @Transactional
        public String performNestedTransactionalOperation() {
            // This should preserve MDC from outer context
            String outerTraceId = MDC.get(LoggingConstants.MDC_TRACE_ID);

            // Call another transactional method
            performInnerTransactionalOperation();

            // MDC should still be available
            return MDC.get(LoggingConstants.MDC_TRACE_ID);
        }

        @Transactional
        private void performInnerTransactionalOperation() {
            // This nested transaction should also preserve MDC
            String traceId = MDC.get(LoggingConstants.MDC_TRACE_ID);
            assertNotNull(traceId, "MDC should be preserved in nested transaction");
        }
    }
}