package de.agiehl.bgg.exception;

/**
 * Thrown when a response body cannot be parsed into the expected XML model.
 */
public class BggParseException extends BggClientException {

    /**
     * Creates a new exception with the given message and cause.
     *
     * @param message the detail message
     * @param cause   the underlying parsing failure
     */
    public BggParseException(String message, Throwable cause) {
        super(message, cause);
    }
}
