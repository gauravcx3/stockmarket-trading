package com.globalbeverage.stockmarket.repository.impl;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import com.globalbeverage.stockmarket.mapper.TradeMapper;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Implementation of {@link TradeRepository} for handling database operations related to {@link Trade} entities.
 */
@Component
public class TradeRepositoryImpl implements TradeRepository {

    private static final Logger logger = LoggerFactory.getLogger(TradeRepositoryImpl.class);
    private final TradeMapper tradeMapper;

    public TradeRepositoryImpl(EntityManager entityManager) {
        this.tradeMapper = new TradeMapper(entityManager);
    }

    /**
     * Finds a trade by its ID.
     *
     * @param id The ID of the trade.
     * @return An Optional containing the trade if found, or an empty Optional if not found.
     */
    @Override
    public Optional<Trade> findTradeById(Long id) {
        logger.info("Looking for trade with ID: {}", id);
        Optional<Trade> trade = tradeMapper.findTradeById(id);
        if (trade.isEmpty()) {
            logger.warn("No trade found with ID: {}", id);
        }
        return trade;
    }

    /**
     * Finds all trades for a specific stock symbol.
     *
     * @param symbol The stock symbol.
     * @return A list of trades related to the given stock symbol.
     */
    @Override
    public List<Trade> findTradesBySymbol(String symbol) {
        logger.info("Finding trades for stock with symbol: {}", symbol);
        List<Trade> trades = tradeMapper.findTradesBySymbol(symbol);
        logger.info("Found {} trades for stock symbol: {}", trades.size(), symbol);
        return trades;
    }

    /**
     * Finds trades for a specific stock symbol within a specific time range.
     *
     * @param symbol The stock symbol.
     * @param startTime The start time of the range.
     * @param endTime The end time of the range.
     * @return A list of trades within the specified time range for the stock symbol.
     */
    @Override
    public List<Trade> findTradesBySymbolAndTimestampBetween(String symbol, LocalDateTime startTime, LocalDateTime endTime) {
        logger.info("Finding trades for stock symbol: {} between {} and {}", symbol, startTime, endTime);
        List<Trade> trades = tradeMapper.findTradesBySymbolAndTimestampBetween(symbol, startTime, endTime);
        logger.info("Found {} trades for stock symbol: {} in the specified time range", trades.size(), symbol);
        return trades;
    }

    /**
     * Saves a trade entity to the database.
     *
     * @param trade The trade entity to be saved.
     * @return The saved trade entity.
     */
    @Override
    public Trade saveTrade(Trade trade) {
        logger.info("Saving trade: {}", trade);
        if (trade.getId() == null) {
            return tradeMapper.saveTrade(trade);
        } else {
            return tradeMapper.saveTrade(trade); // update is handled inside the saveTrade method.
        }
    }

    /**
     * Saves a list of trades to the database.
     *
     * @param trades The list of trades to be saved.
     * @return The list of saved trade entities.
     */
    @Override
    public List<Trade> saveAllTrades(List<Trade> trades) {
        logger.info("Saving multiple trades. Count: {}", trades.size());
        return tradeMapper.saveAllTrades(trades);
    }

    /**
     * Deletes a trade entity from the database.
     *
     * @param trade The trade entity to be deleted.
     */
    @Override
    public void deleteTrade(Trade trade) {
        logger.info("Attempting to delete trade: {}", trade);
        tradeMapper.deleteTrade(trade);
    }

    /**
     * Deletes a trade by its ID.
     *
     * @param id The ID of the trade to be deleted.
     */
    @Override
    public void deleteTradeById(Long id) {
        logger.info("Deleting trade by ID: {}", id);
        tradeMapper.deleteTradeById(id);
    }
}
