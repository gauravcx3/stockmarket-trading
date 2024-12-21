package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
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
     * @throws IllegalArgumentException if the trade data is invalid (e.g., price or quantity <= 0).
     */
    void recordTrade(Trade trade) throws IllegalArgumentException;

    /**
     * Retrieves a list of trades for a specific stock.
     *
     * @param stockSymbol The stock's symbol.
     * @return A list of trades for the stock, or an empty list if none found.
     */
    List<Trade> getTradesForStock(String stockSymbol);

    /**
     * Retrieves a list of trades for a stock, throwing an exception if no trades are found.
     *
     * @param stockSymbol The stock's symbol.
     * @return A list of trades for the stock.
     * @throws StockNotFoundException if no trades are found.
     */
    Optional<List<Trade>> getTradesForStockOptional(String stockSymbol) throws StockNotFoundException;
}
