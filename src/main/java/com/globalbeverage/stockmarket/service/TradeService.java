package com.globalbeverage.stockmarket.service;

public interface TradeService {
    void recordTrade(String stockSymbol, int quantity, boolean isBuy, double price);
    double calculateVolumeWeightedStockPrice(String stockSymbol);
}
