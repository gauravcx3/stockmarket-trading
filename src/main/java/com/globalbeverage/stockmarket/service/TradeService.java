package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Trade;

import java.util.List;

public interface TradeService {
    void recordTrade(Trade trade);
    List<Trade> getTradesForStock(String stockSymbol);
}