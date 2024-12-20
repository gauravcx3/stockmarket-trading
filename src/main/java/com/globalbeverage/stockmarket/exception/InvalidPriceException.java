package com.globalbeverage.stockmarket.exception;

/**
 * Exception thrown when an invalid price is provided for calculations.
 */
public class InvalidPriceException extends RuntimeException {
    public InvalidPriceException(String message) {
        super(message);
    }
}
