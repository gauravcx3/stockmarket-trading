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
 * Implementation of the StockService interface for calculating stock-related financial metrics.
 */
@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private StockRepository stockRepository;

    /**
     * Calculates the dividend yield for a stock.
     *
     * @param symbol The stock's symbol.
     * @param price The stock's current market price.
     * @return The dividend yield.
     * @throws StockNotFoundException if the stock is not found.
     * @throws InvalidPriceException if the price is invalid (<= 0).
     */
    @Override
    public double calculateDividendYield(String symbol, double price) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.error("Stock with symbol {} not found.", symbol);
                    return new StockNotFoundException(symbol);
                });

        if (price <= 0) {
            logger.error("Invalid price {} for stock {}.", price, symbol);
            throw new InvalidPriceException();
        }

        if ("COMMON".equals(stock.getType())) {
            return stock.getLastDividend() / price;
        } else {
            return (stock.getFixedDividend() * stock.getParValue()) / price;
        }
    }

    /**
     * Calculates the P/E ratio for a stock.
     *
     * @param symbol The stock's symbol.
     * @param price The stock's current market price.
     * @return The P/E ratio.
     * @throws StockNotFoundException if the stock is not found.
     * @throws InvalidPriceException if the price is invalid (<= 0).
     */
    @Override
    public double calculatePERatio(String symbol, double price) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.error("Stock with symbol {} not found.", symbol);
                    return new StockNotFoundException(symbol);
                });

        if (price <= 0) {
            logger.error("Invalid price {} for stock {}.", price, symbol);
            throw new InvalidPriceException();
        }

        if (stock.getLastDividend() == 0) {
            logger.warn("No dividend for stock {}. Returning NaN for P/E ratio.", symbol);
            return Double.NaN;
        }

        return price / stock.getLastDividend();
    }

    /**
     * Calculates the VWAP (Volume-Weighted Average Price) for a stock based on trades in the last 5 minutes.
     *
     * @param symbol The stock's symbol.
     * @return The VWAP.
     * @throws StockNotFoundException if the stock is not found.
     */
    @Override
    public double calculateVWSP(String symbol) {
        Stock stock = stockRepository.findBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.error("Stock with symbol {} not found.", symbol);
                    return new StockNotFoundException(symbol);
                });

        List<Trade> trades = stock.getTrades();
        LocalDateTime fiveMinutesAgo = LocalDateTime.now().minus(5, ChronoUnit.MINUTES);
        trades = trades.stream()
                .filter(trade -> trade.getTimestamp().isAfter(fiveMinutesAgo))
                .collect(Collectors.toList());

        if (trades.isEmpty()) return 0;

        double totalValue = 0;
        int totalQuantity = 0;

        for (Trade trade : trades) {
            totalValue += trade.getPrice() * trade.getQuantity();
            totalQuantity += trade.getQuantity();
        }

        return totalValue / totalQuantity;
    }

    /**
     * Calculates the GBCE All Share Index based on the geometric mean of VWAPs of all stocks.
     *
     * @return The GBCE All Share Index.
     */
    @Override
    public double calculateGBCEAllShareIndex() {
        List<Stock> stocks = stockRepository.findAll();
        double productOfVWSP = 1.0;
        int count = 0;

        for (Stock stock : stocks) {
            double vwsp = calculateVWSP(stock.getSymbol());
            productOfVWSP *= vwsp;
            count++;
        }

        return (count > 0) ? Math.pow(productOfVWSP, 1.0 / count) : 0.0;
    }
}
