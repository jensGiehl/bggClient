package de.agiehl.bgg.exception;

/**
 * Thrown when a website login fails or does not establish an authenticated session.
 * Login response bodies are deliberately omitted to protect credentials and cookies.
 */
public class BggAuthenticationException extends BggClientException {

    /**
     * Creates an authentication failure without exposing the server response.
     *
     * @param message the description of the failure
     */
    public BggAuthenticationException(String message) {
        super(message);
    }
}
