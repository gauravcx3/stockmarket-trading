package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

/**
 * Implementation of the TradeService interface.
 * Provides methods to record trades and retrieve trades for a specific stock.
 */
@Service
public class TradeServiceImpl implements TradeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);

    @Autowired
    private TradeRepository tradeRepository;

    /**
     * Records a new trade.
     * Validates the trade data and throws an exception if the price or quantity is invalid.
     *
     * @param trade The trade object to be recorded.
     * @throws IllegalArgumentException if the trade price or quantity is invalid.
     */
    @Override
    public void recordTrade(Trade trade) {
        if (trade.getPrice() <= 0) {
            logger.error("Invalid trade price: {} for stock: {}", trade.getPrice(), trade.getStockSymbol());
            throw new IllegalArgumentException("Trade price must be greater than 0");
        }
        if (trade.getQuantity() <= 0) {
            logger.error("Invalid trade quantity: {} for stock: {}", trade.getQuantity(), trade.getStockSymbol());
            throw new IllegalArgumentException("Trade quantity must be greater than 0");
        }
        tradeRepository.save(trade);
        logger.info("Successfully recorded trade for stock: {} with price: {} and quantity: {}",
                trade.getStockSymbol(), trade.getPrice(), trade.getQuantity());
    }

    /**
     * Retrieves a list of trades for a specific stock.
     * Throws a StockNotFoundException if no trades are found for the given stock.
     *
     * @param stockSymbol The symbol of the stock for which trades are to be retrieved.
     * @return A list of trades associated with the given stock symbol.
     * @throws StockNotFoundException if no trades exist for the given stock symbol.
     */
    @Override
    public List<Trade> getTradesForStock(String stockSymbol) throws StockNotFoundException {
        List<Trade> trades = tradeRepository.findByStockSymbol(stockSymbol);
        if (trades.isEmpty()) {
            logger.error("No trades found for stock: {}", stockSymbol);
            throw new StockNotFoundException("No trades found for stock: " + stockSymbol);
        }
        logger.info("Retrieved {} trades for stock: {}", trades.size(), stockSymbol);
        return trades;
    }

    /**
     * Retrieves a list of trades for a specific stock, or returns an empty list if no trades exist.
     *
     * @param stockSymbol The symbol of the stock for which trades are to be retrieved.
     * @return A list of trades associated with the given stock symbol, or an empty list if no trades exist.
     */
    @Override
    public Optional<List<Trade>> getTradesForStockOptional(String stockSymbol) {
        List<Trade> trades = tradeRepository.findByStockSymbol(stockSymbol);
        if (trades.isEmpty()) {
            logger.warn("No trades found for stock: {}", stockSymbol);
            return Optional.empty();
        }
        logger.info("Retrieved {} trades for stock: {}", trades.size(), stockSymbol);
        return Optional.of(trades);
    }
}
