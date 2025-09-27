package com.carlosmgv02.logginglibrary.unit;

import com.carlosmgv02.logginglibrary.shared.util.MessageFormatter;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageFormatterTest {

    @Test
    void shouldFormatMessageWithSinglePlaceholder() {
        String result = MessageFormatter.format("Hello {}", "World");
        assertEquals("Hello World", result);
    }

    @Test
    void shouldFormatMessageWithMultiplePlaceholders() {
        String result = MessageFormatter.format("User {} has {} items", "John", 5);
        assertEquals("User John has 5 items", result);
    }

    @Test
    void shouldFormatMessageWithNoPlaceholders() {
        String result = MessageFormatter.format("Simple message", "unused");
        assertEquals("Simple message", result);
    }

    @Test
    void shouldFormatMessageWithNoArguments() {
        String result = MessageFormatter.format("Message with {} placeholder");
        assertEquals("Message with {} placeholder", result);
    }

    @Test
    void shouldFormatMessageWithNullArgument() {
        String result = MessageFormatter.format("Value: {}", (Object) null);
        assertEquals("Value: null", result);
    }

    @Test
    void shouldFormatMessageWithMorePlaceholdersThanArguments() {
        String result = MessageFormatter.format("First: {}, Second: {}", "one");
        assertEquals("First: one, Second: {}", result);
    }

    @Test
    void shouldFormatMessageWithMoreArgumentsThanPlaceholders() {
        String result = MessageFormatter.format("Value: {}", "one", "two");
        assertEquals("Value: one", result);
    }

    @Test
    void shouldFormatPaginationMessage() {
        String result = MessageFormatter.format("Finding all plantations with pagination: page {}, size {}", 0, 20);
        assertEquals("Finding all plantations with pagination: page 0, size 20", result);
    }

    @Test
    void shouldHandleThrowableInFormatWithThrowable() {
        Exception ex = new RuntimeException("Test error");
        MessageFormatter.FormattedMessage result = MessageFormatter.formatWithThrowable("Error occurred: {}", "during processing", ex);

        assertEquals("Error occurred: during processing", result.getMessage());
        assertTrue(result.hasThrowable());
        assertEquals(ex, result.getThrowable());
    }

    @Test
    void shouldHandleThrowableWithoutExtraArguments() {
        Exception ex = new RuntimeException("Test error");
        MessageFormatter.FormattedMessage result = MessageFormatter.formatWithThrowable("Error occurred", ex);

        assertEquals("Error occurred", result.getMessage());
        assertTrue(result.hasThrowable());
        assertEquals(ex, result.getThrowable());
    }

    @Test
    void shouldNotTreatThrowableAsArgumentWhenNotLastAndFitsInPlaceholders() {
        Exception ex = new RuntimeException("Test error");
        MessageFormatter.FormattedMessage result = MessageFormatter.formatWithThrowable("Error: {} message: {}", ex, "additional info");

        assertEquals("Error: Test error message: additional info", result.getMessage());
        assertFalse(result.hasThrowable());
    }

    @Test
    void shouldHandleNullMessagePattern() {
        String result = MessageFormatter.format(null, "arg");
        assertNull(result);
    }

    @Test
    void shouldHandleNullArguments() {
        String result = MessageFormatter.format("Message {}", (Object[]) null);
        assertEquals("Message {}", result);
    }
}