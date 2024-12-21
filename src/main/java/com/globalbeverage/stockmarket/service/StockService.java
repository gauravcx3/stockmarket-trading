package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.exception.InvalidPriceException;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;

/**
 * Service interface for calculating financial metrics for stocks.
 * Includes methods for calculating:
 * - Dividend Yield
 * - P/E Ratio
 * - VWAP
 * - GBCE All Share Index
 */
public interface StockService {

    /**
     * Calculates the dividend yield for a stock.
     *
     * @param symbol The stock's symbol.
     * @param price The stock's current market price.
     * @return The dividend yield.
     * @throws StockNotFoundException if the stock is not found.
     * @throws InvalidPriceException if the price is invalid (<= 0).
     */
    double calculateDividendYield(String symbol, double price) throws StockNotFoundException, InvalidPriceException;

    /**
     * Calculates the Price-to-Earnings (P/E) ratio for a stock.
     *
     * @param symbol The stock's symbol.
     * @param price The stock's current market price.
     * @return The P/E ratio.
     * @throws StockNotFoundException if the stock is not found.
     */
    double calculatePERatio(String symbol, double price) throws StockNotFoundException;

    /**
     * Calculates the Volume-Weighted Average Price (VWAP) for a stock.
     *
     * @param symbol The stock's symbol.
     * @return The VWAP.
     * @throws StockNotFoundException if the stock is not found.
     */
    double calculateVWSP(String symbol) throws StockNotFoundException;

    /**
     * Calculates the GBCE All Share Index.
     *
     * @return The GBCE All Share Index.
     */
    double calculateGBCEAllShareIndex();
}
