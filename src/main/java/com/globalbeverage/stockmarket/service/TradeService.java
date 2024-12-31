package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing trade operations.
 */
public interface TradeService {

    /**
     * Records a new trade.
     *
     * @param trade The trade object to be recorded.
     * @throws IllegalArgumentException if the trade data is invalid (e.g., price <= 0 for buy trades).
     * @throws jakarta.validation.ConstraintViolationException if validation constraints are violated.
     */
    void recordTrade(Trade trade) throws IllegalArgumentException;

    /**
     * Retrieves a list of trades for a specific stock.
     *
     * @param stockSymbol The stock's symbol.
     * @return A list of trades for the stock, or an empty list if none found.
     * @throws StockNotFoundException if no trades are found for the stock.
     */
    List<Trade> getTradesForStock(String stockSymbol) throws StockNotFoundException;

    /**
     * Retrieves a list of trades for a specific stock, returning an optional result.
     *
     * @param stockSymbol The stock's symbol.
     * @return An Optional containing a list of trades, or empty if no trades are found.
     */
    Optional<List<Trade>> getTradesForStockOptional(String stockSymbol);

    /**
     * Retrieves a list of trades for a specific stock within a specified time range.
     *
     * @param stockSymbol The stock's symbol.
     * @param start       The start time of the range.
     * @param end         The end time of the range.
     * @return A list of trades within the time range, or an empty list if none are found.
     */
    List<Trade> getTradesForStockWithinTimeRange(String stockSymbol, LocalDateTime start, LocalDateTime end);
}
