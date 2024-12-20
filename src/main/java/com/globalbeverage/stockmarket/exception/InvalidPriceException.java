package com.globalbeverage.stockmarket.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Exception thrown when an invalid price is provided for calculations.
 */
public class InvalidPriceException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(InvalidPriceException.class);

    /**
     * Constructs a new InvalidPriceException with a specific error message.
     *
     * @param message The detail message to describe the exception.
     */
    public InvalidPriceException(String message) {
        super(message);
        logger.error("Invalid price error: " + message);  // Log the error with the message
    }

    /**
     * Constructs a new InvalidPriceException with a default error message.
     */
    public InvalidPriceException() {
        super("The price provided is invalid. It must be greater than zero.");
        logger.error("Invalid price error: The price provided is invalid. It must be greater than zero.");  // Log the default error
    }
}
