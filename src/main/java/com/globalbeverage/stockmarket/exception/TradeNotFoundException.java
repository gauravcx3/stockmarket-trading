package com.globalbeverage.stockmarket.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Custom exception to be thrown when a trade cannot be found.
 * Extends RuntimeException to allow unchecked exceptions.
 */
public class TradeNotFoundException extends RuntimeException {

    private static final Logger logger = LoggerFactory.getLogger(TradeNotFoundException.class);

    /**
     * Constructor to create a new TradeNotFoundException with a specific message.
     *
     * @param message The detail message that describes the reason for the exception.
     */
    public TradeNotFoundException(String message) {
        super(message);
        logger.error("Trade not found: " + message);
    }

    /**
     * Constructor to create a new TradeNotFoundException with a default message.
     */
    public TradeNotFoundException() {
        super("The specified trade was not found.");
        logger.error("Trade not found: The specified trade was not found.");
    }
}
