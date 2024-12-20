package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.InvalidPriceException;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Implementation of the StockService interface.
 * Provides methods for calculating dividend yield, P/E ratio, and volume-weighted average price (VWAP) for stocks.
 */
@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private StockRepository stockRepository;

    /**
     * Calculates the dividend yield for a given stock.
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @param price The current market price of the stock.
     * @return The dividend yield as a double.
     * @throws StockNotFoundException If the stock is not found in the repository.
     * @throws InvalidPriceException If the price is less than or equal to 0.
     */
    @Override
    public double calculateDividendYield(String symbol, double price) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.error("Stock with symbol {} not found.", symbol);
                    return new StockNotFoundException(symbol);
                });

        if (price <= 0) {
            logger.error("Invalid price {} provided for stock with symbol {}.", price, symbol);
            throw new InvalidPriceException();
        }

        if ("COMMON".equals(stock.getType())) {
            return stock.getLastDividend() / price;
        } else { // PREFERRED
            return (stock.getFixedDividend() * stock.getParValue()) / price;
        }
    }

    /**
     * Calculates the P/E ratio for a given stock.
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @param price The current market price of the stock.
     * @return The P/E ratio as a double.
     * @throws StockNotFoundException If the stock is not found in the repository.
     * @throws InvalidPriceException If the price is less than or equal to 0.
     */
    @Override
    public double calculatePERatio(String symbol, double price) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.error("Stock with symbol {} not found.", symbol);
                    return new StockNotFoundException(symbol);
                });

        if (price <= 0) {
            logger.error("Invalid price {} provided for stock with symbol {}.", price, symbol);
            throw new InvalidPriceException();
        }

        if (stock.getLastDividend() == 0) return 0;

        return price / stock.getLastDividend();
    }

    /**
     * Calculates the volume-weighted average price (VWAP) for a given stock based on its trades in the last 5 minutes.
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @return The VWAP as a double.
     * @throws StockNotFoundException If the stock is not found in the repository.
     */
    @Override
    public double calculateVWSP(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.error("Stock with symbol {} not found.", symbol);
                    return new StockNotFoundException(symbol);
                });

        List<Trade> trades = stock.getTrades();

        // Filter trades to only include those in the last 5 minutes
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);
        trades = trades.stream()
                .filter(trade -> trade.getTimestamp().isAfter(fiveMinutesAgo))
                .collect(Collectors.toList());

        if (trades.isEmpty()) return 0;  // Return 0 if there are no trades in the last 5 minutes

        double totalValue = 0;
        int totalQuantity = 0;

        for (Trade trade : trades) {
            totalValue += trade.getPrice() * trade.getQuantity();
            totalQuantity += trade.getQuantity();
        }

        return totalValue / totalQuantity;
    }
}
