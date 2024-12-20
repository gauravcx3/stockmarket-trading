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
        super(message);
        logger.error("Stock not found: " + message);  // Log the error message when the exception is thrown
    }

    /**
     * Constructor to create a new StockNotFoundException with a specific message and cause.
     *
     * @param message The detail message that describes the reason for the exception.
     * @param cause   The cause of the exception (can be used for chaining exceptions).
     */
    public StockNotFoundException(String message, Throwable cause) {
        super(message, cause);
        logger.error("Stock not found: " + message, cause);  // Log the error with the cause when provided
    }

    /**
     * Constructor to create a new StockNotFoundException with a default message.
     */
    public StockNotFoundException() {
        super("The specified stock was not found.");
        logger.error("Stock not found: The specified stock was not found.");  // Log the default error
    }
}
