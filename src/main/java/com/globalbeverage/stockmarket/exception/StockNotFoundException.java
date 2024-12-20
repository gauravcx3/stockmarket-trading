package com.globalbeverage.stockmarket.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception thrown when a stock is not found in the repository.
 * This is typically thrown when attempting to perform operations on a stock that doesn't exist.
 * Extends RuntimeException to allow unchecked exceptions.
 */
public class StockNotFoundException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(StockNotFoundException.class);

    /**
     * Constructor to create a new StockNotFoundException with a specific message.
     *
     * @param message The detail message that describes the reason for the exception.
     */
    public StockNotFoundException(String message) {
        super("Stock not found: " + message);  // Prefix the message here
        logger.error("Stock not found: " + message);  // Log the full message when the exception is thrown
    }

    /**
     * Constructor to create a new StockNotFoundException with a specific message and cause.
     *
     * @param message The detail message that describes the reason for the exception.
     * @param cause   The cause of the exception (can be used for chaining exceptions).
     */
    public StockNotFoundException(String message, Throwable cause) {
        super("Stock not found: " + message, cause);  // Prefix the message here
        logger.error("Stock not found: " + message, cause);  // Log the full message with the cause
    }

    /**
     * Constructor to create a new StockNotFoundException with a default message.
     */
    public StockNotFoundException() {
        super("Stock not found: The specified stock was not found.");  // Prefix the default message
        logger.error("Stock not found: The specified stock was not found.");  // Log the default error
    }
}
