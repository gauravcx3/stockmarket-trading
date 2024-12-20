package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;

import java.util.List;
import java.util.Optional;

/**
 * Service interface for managing trade operations.
 * Provides methods for recording trades and retrieving trades for a specific stock.
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
     * @param stockSymbol The symbol of the stock for which trades are to be retrieved.
     * @return A list of trades associated with the given stock symbol, or an empty list if no trades are found.
     */
    List<Trade> getTradesForStock(String stockSymbol);

    /**
     * Retrieves a list of trades for a specific stock, or throws an exception if no trades exist.
     *
     * @param stockSymbol The symbol of the stock for which trades are to be retrieved.
     * @return A list of trades associated with the given stock symbol.
     * @throws StockNotFoundException if no trades exist for the given stock symbol.
     */
    Optional<List<Trade>> getTradesForStockOptional(String stockSymbol) throws StockNotFoundException;
}
