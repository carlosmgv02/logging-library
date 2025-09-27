package com.carlosmgv02.logginglibrary.shared.util;

public final class MessageFormatter {

    private static final String PLACEHOLDER = "{}";

    private MessageFormatter() {
        throw new IllegalStateException("Utility class");
    }

    /**
     * Formats a message pattern with arguments, replacing {} placeholders.
     * This method mimics the behavior of SLF4J's message formatting.
     *
     * @param messagePattern The message pattern containing {} placeholders
     * @param arguments The arguments to substitute into the pattern
     * @return The formatted message
     */
    public static String format(String messagePattern, Object... arguments) {
        if (messagePattern == null) {
            return null;
        }

        if (arguments == null || arguments.length == 0) {
            return messagePattern;
        }

        StringBuilder result = new StringBuilder(messagePattern.length() + 50);
        int argIndex = 0;
        int start = 0;

        while (true) {
            int placeholderIndex = messagePattern.indexOf(PLACEHOLDER, start);

            if (placeholderIndex == -1) {
                // No more placeholders, append the rest of the message
                result.append(messagePattern.substring(start));
                break;
            }

            // Append text before the placeholder
            result.append(messagePattern.substring(start, placeholderIndex));

            // Replace placeholder with argument if available
            if (argIndex < arguments.length) {
                Object arg = arguments[argIndex];
                if (arg == null) {
                    result.append("null");
                } else if (arg instanceof Throwable) {
                    // For throwables, just append the message, not the full stack trace
                    result.append(((Throwable) arg).getMessage());
                } else {
                    result.append(arg.toString());
                }
                argIndex++;
            } else {
                // No more arguments, keep the placeholder
                result.append(PLACEHOLDER);
            }

            start = placeholderIndex + PLACEHOLDER.length();
        }

        return result.toString();
    }

    /**
     * Formats a message pattern with arguments and returns both the formatted message
     * and any remaining throwable argument.
     *
     * @param messagePattern The message pattern containing {} placeholders
     * @param arguments The arguments to substitute into the pattern
     * @return A FormattedMessage containing the formatted text and optional throwable
     */
    public static FormattedMessage formatWithThrowable(String messagePattern, Object... arguments) {
        if (arguments == null || arguments.length == 0) {
            return new FormattedMessage(messagePattern, null);
        }

        // Check if the last argument is a throwable and should not be formatted
        Object lastArg = arguments[arguments.length - 1];
        Throwable throwable = null;
        Object[] formatArgs = arguments;

        // Count placeholders in the message
        int placeholderCount = countPlaceholders(messagePattern);

        // If we have more arguments than placeholders and the last one is a throwable,
        // treat it as the exception parameter
        if (arguments.length > placeholderCount && lastArg instanceof Throwable) {
            throwable = (Throwable) lastArg;
            formatArgs = new Object[arguments.length - 1];
            System.arraycopy(arguments, 0, formatArgs, 0, formatArgs.length);
        }

        String formattedMessage = format(messagePattern, formatArgs);
        return new FormattedMessage(formattedMessage, throwable);
    }

    private static int countPlaceholders(String messagePattern) {
        if (messagePattern == null) {
            return 0;
        }

        int count = 0;
        int index = 0;
        while ((index = messagePattern.indexOf(PLACEHOLDER, index)) != -1) {
            count++;
            index += PLACEHOLDER.length();
        }
        return count;
    }

    public static class FormattedMessage {
        private final String message;
        private final Throwable throwable;

        public FormattedMessage(String message, Throwable throwable) {
            this.message = message;
            this.throwable = throwable;
        }

        public String getMessage() {
            return message;
        }

        public Throwable getThrowable() {
            return throwable;
        }

        public boolean hasThrowable() {
            return throwable != null;
        }
    }
}