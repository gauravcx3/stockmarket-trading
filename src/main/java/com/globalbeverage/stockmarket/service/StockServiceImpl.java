package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.exception.InvalidPriceException;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class StockServiceImpl implements StockService {
    @Autowired
    private StockRepository stockRepository;

    @Override
    public double calculateDividendYield(String symbol, double price) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(symbol));

        if (price <= 0) throw new InvalidPriceException();

        if ("COMMON".equals(stock.getType())) {
            return stock.getLastDividend() / price;
        } else { // PREFERRED
            return (stock.getFixedDividend() * stock.getParValue()) / price;
        }
    }

    @Override
    public double calculatePERatio(String symbol, double price) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(symbol));

        if (stock.getLastDividend() == 0) return 0;

        return price / stock.getLastDividend();
    }

    @Override
    public double calculateVWSP(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> new StockNotFoundException(symbol));

        List<Trade> trades = stock.getTrades();
        if (trades.isEmpty()) return 0;

        double totalValue = 0;
        int totalQuantity = 0;

        for (Trade trade : trades) {
            totalValue += trade.getPrice() * trade.getQuantity();
            totalQuantity += trade.getQuantity();
        }

        return totalValue / totalQuantity;
    }
}
