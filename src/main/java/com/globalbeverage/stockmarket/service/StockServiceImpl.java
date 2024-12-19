package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    private StockRepository stockRepository;

    @Override
    public double calculateDividendYield(String symbol, double price) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        if (price <= 0) throw new IllegalArgumentException("Price must be greater than 0");

        if ("COMMON".equals(stock.getType())) {
            return stock.getLastDividend() / price;
        } else { // PREFERRED
            return (stock.getFixedDividend() * stock.getParValue()) / price;
        }
    }

    @Override
    public double calculatePERatio(String symbol, double price) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        if (stock.getLastDividend() == 0) return 0;

        return price / stock.getLastDividend();
    }

    public double calculateVWSP(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new RuntimeException("Stock not found"));

        double totalValue = 0;
        int totalQuantity = 0;

        for (Trade trade : stock.getTrades()) {
            totalValue += trade.getPrice() * trade.getQuantity();
            totalQuantity += trade.getQuantity();
        }

        if (totalQuantity == 0) {
            throw new IllegalArgumentException("No trades found for stock " + symbol);
        }

        return totalValue / totalQuantity;
    }
}
