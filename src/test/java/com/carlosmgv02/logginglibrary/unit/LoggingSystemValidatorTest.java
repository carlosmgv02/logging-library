package com.carlosmgv02.logginglibrary.unit;

import com.carlosmgv02.logginglibrary.infrastructure.validation.LoggingSystemValidator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
class LoggingSystemValidatorTest {

    @Autowired
    private LoggingSystemValidator validator;

    @Test
    void whenApplicationStarts_thenLoggingSystemValidationPasses() {
        assertDoesNotThrow(() -> validator.validateLoggingSystemOnStartup());
    }
}