package com.globalbeverage.stockmarket.repository;

import com.globalbeverage.stockmarket.domain.Trade;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Trade} entities.
 */
public interface TradeRepository {

    /**
     * Finds a trade by its ID.
     *
     * @param id The ID of the trade.
     * @return An Optional containing the trade if found, or an empty Optional if not found.
     */
    Optional<Trade> findTradeById(Long id);

    /**
     * Finds all trades for a specific stock symbol.
     *
     * @param symbol The stock symbol.
     * @return A list of trades related to the given stock symbol.
     */
    List<Trade> findTradesBySymbol(String symbol);

    /**
     * Saves a trade entity to the database.
     *
     * @param trade The trade entity to be saved.
     * @return The saved trade entity.
     */
    Trade saveTrade(Trade trade);

    /**
     * Saves a list of trades to the database.
     *
     * @param trades The list of trades to be saved.
     * @return The list of saved trade entities.
     */
    List<Trade> saveAllTrades(List<Trade> trades);

    /**
     * Deletes a trade entity from the database.
     *
     * @param trade The trade entity to be deleted.
     */
    void deleteTrade(Trade trade);

    /**
     * Deletes a trade by its ID.
     *
     * @param id The ID of the trade to be deleted.
     */
    void deleteTradeById(Long id);

    /**
     * Finds trades for a specific stock symbol within a specified time range.
     *
     * @param symbol The stock symbol.
     * @param start The start of the time range.
     * @param end The end of the time range.
     * @return A list of trades matching the criteria.
     */
    List<Trade> findTradesBySymbolAndTimestampBetween(String symbol, LocalDateTime start, LocalDateTime end);
}
