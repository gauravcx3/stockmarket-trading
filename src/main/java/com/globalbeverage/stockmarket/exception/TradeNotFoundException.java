package com.globalbeverage.stockmarket.exception;

public class TradeNotFoundException extends RuntimeException {
    public TradeNotFoundException(String message) {
        super(message);
    }
}
