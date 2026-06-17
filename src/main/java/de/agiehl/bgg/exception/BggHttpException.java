package de.agiehl.bgg.exception;

import lombok.Getter;

/**
 * Thrown when the BoardGameGeek server responds with a non-success HTTP status.
 *
 * <p>The exception carries the HTTP status code and the raw response body so that
 * callers can react to specific situations such as {@code 202 Accepted} (queued
 * responses for the collection endpoint) or rate limiting.
 */
@Getter
public class BggHttpException extends BggClientException {

    /** HTTP status code returned by the server. */
    private final int statusCode;

    /** Raw response body returned by the server, may be empty. */
    private final String responseBody;

    /**
     * Creates a new exception.
     *
     * @param statusCode   the HTTP status code
     * @param responseBody the raw response body, never {@code null}
     */
    public BggHttpException(int statusCode, String responseBody) {
        super("BGG API responded with HTTP " + statusCode);
        this.statusCode = statusCode;
        this.responseBody = responseBody == null ? "" : responseBody;
    }
}
