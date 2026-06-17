package de.agiehl.bgg.exception;

/**
 * Base exception type for all errors raised by the BGG client.
 *
 * <p>All other exceptions in this library extend this class, so a single
 * {@code catch} clause is sufficient to handle every failure mode.
 */
public class BggClientException extends RuntimeException {

    /**
     * Creates a new exception with the given message.
     *
     * @param message the detail message
     */
    public BggClientException(String message) {
        super(message);
    }

    /**
     * Creates a new exception with the given message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying cause
     */
    public BggClientException(String message, Throwable cause) {
        super(message, cause);
    }
}
