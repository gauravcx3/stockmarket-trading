package com.globalbeverage.stockmarket.exception;

/**
 * Exception thrown when a stock is not found in the repository.
 */
public class StockNotFoundException extends RuntimeException {
    public StockNotFoundException(String message) {
        super(message);
    }
}
