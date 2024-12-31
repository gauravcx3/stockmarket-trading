package com.globalbeverage.stockmarket.service.impl;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.InvalidPriceException;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.StockRepository;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import com.globalbeverage.stockmarket.service.StockService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

/**
 * Implementation of the StockService interface for calculating stock-related financial metrics.
 */
@Service
public class StockServiceImpl implements StockService {

    private static final Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);

    @Autowired
    private StockRepository stockRepository;

    @Autowired
    private TradeRepository tradeRepository;

    /**
     * Calculates the dividend yield for a given stock based on its type.
     *
     * @param symbol The stock's symbol.
     * @param price  The current market price of the stock.
     * @return The calculated dividend yield.
     * @throws StockNotFoundException If the stock with the given symbol is not found.
     * @throws InvalidPriceException  If the given price is less than or equal to zero.
     */
    @Override
    public double calculateDividendYield(String symbol, double price) {
        Stock stock = stockRepository.findStockBySymbol(symbol)
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
     * Calculates the P/E (Price-to-Earnings) ratio for a given stock.
     *
     * @param symbol The stock's symbol.
     * @param price  The current market price of the stock.
     * @return The calculated P/E ratio.
     * @throws StockNotFoundException If the stock with the given symbol is not found.
     * @throws InvalidPriceException  If the given price is less than or equal to zero.
     */
    @Override
    public double calculatePERatio(String symbol, double price) {
        Stock stock = stockRepository.findStockBySymbol(symbol)
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
     * Calculates the Volume Weighted Stock Price (VWSP) for a stock based on trades
     * in the last 5 minutes.
     *
     * @param symbol The stock's symbol.
     * @return The calculated VWSP. Returns 0 if no trades are found within the last 5 minutes.
     * @throws StockNotFoundException If the stock with the given symbol is not found.
     */
    @Override
    public double calculateVWSP(String symbol) {
        Stock stock = stockRepository.findStockBySymbol(symbol)
                .orElseThrow(() -> {
                    logger.error("Stock with symbol {} not found.", symbol);
                    return new StockNotFoundException(symbol);
                });

        LocalDateTime now = LocalDateTime.now();
        LocalDateTime fiveMinutesAgo = now.minus(5, ChronoUnit.MINUTES);

        List<Trade> trades = tradeRepository.findTradesBySymbolAndTimestampBetween(symbol, fiveMinutesAgo, now);

        if (trades.isEmpty()) {
            logger.warn("No trades found for stock {} in the last 5 minutes.", symbol);
            return 0;
        }

        double totalValue = trades.stream()
                .mapToDouble(trade -> trade.getPrice() * trade.getQuantity())
                .sum();
        int totalQuantity = trades.stream()
                .mapToInt(Trade::getQuantity)
                .sum();

        return totalQuantity > 0 ? totalValue / totalQuantity : 0;
    }

    /**
     * Calculates the GBCE All Share Index, which is the geometric mean of the VWSPs
     * of all stocks in the market.
     *
     * @return The calculated GBCE All Share Index. Returns 0 if no stocks are available.
     */
    @Override
    public double calculateGBCEAllShareIndex() {
        List<Stock> stocks = stockRepository.findAllStocks();

        if (stocks.isEmpty()) {
            logger.warn("No stocks available for GBCE All Share Index calculation.");
            return 0;
        }

        double productOfVWSP = stocks.stream()
                .mapToDouble(stock -> {
                    double vwsp = calculateVWSP(stock.getSymbol());
                    return vwsp > 0 ? vwsp : 1;
                })
                .reduce(1.0, (a, b) -> a * b);

        return Math.pow(productOfVWSP, 1.0 / stocks.size());
    }
}
