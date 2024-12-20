package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TradeServiceImpl implements TradeService {

    @Autowired
    private TradeRepository tradeRepository;

    @Override
    public void recordTrade(Trade trade) {
        if (trade.getPrice() <= 0) {
            throw new IllegalArgumentException("Trade price must be greater than 0");
        }
        if (trade.getQuantity() <= 0) {
            throw new IllegalArgumentException("Trade quantity must be greater than 0");
        }
        tradeRepository.save(trade);
    }

    @Override
    public List<Trade> getTradesForStock(String stockSymbol) {
        return tradeRepository.findByStockSymbol(stockSymbol);
    }
}
