package com.fmis.fmis_proxy_intf.fmis_proxy_intf.exception;

/**
 * Custom exception to handle cases where a resource is not found.
 */
public class ResourceNotFoundException extends RuntimeException {

    /**
     * Constructor with a message to describe the exception.
     *
     * @param message Detailed message explaining the exception.
     */
    public ResourceNotFoundException(String message) {
        super(message);
    }

    /**
     * Constructor with both a message and a cause to provide more context.
     *
     * @param message Detailed message explaining the exception.
     * @param cause   The underlying cause of the exception.
     */
    public ResourceNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
