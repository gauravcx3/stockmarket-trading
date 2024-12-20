package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.exception.InvalidPriceException;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;

/**
 * Service interface for calculating financial metrics related to stocks.
 * Provides methods for calculating dividend yield, P/E ratio, and volume-weighted average price (VWAP).
 */
public interface StockService {

    /**
     * Calculates the dividend yield for a stock based on its symbol and the current market price.
     * The dividend yield is calculated as the annual dividend divided by the stock price.
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @param price The current market price of the stock.
     * @return The dividend yield as a double.
     * @throws StockNotFoundException if the stock is not found in the repository.
     * @throws InvalidPriceException if the price is less than or equal to zero.
     */
    double calculateDividendYield(String symbol, double price) throws StockNotFoundException, InvalidPriceException;

    /**
     * Calculates the Price-to-Earnings (P/E) ratio for a stock based on its symbol and the current market price.
     * The P/E ratio is calculated as the stock price divided by the earnings per share (EPS).
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @param price The current market price of the stock.
     * @return The P/E ratio as a double.
     * @throws StockNotFoundException if the stock is not found in the repository.
     */
    double calculatePERatio(String symbol, double price) throws StockNotFoundException;

    /**
     * Calculates the volume-weighted average price (VWAP) for a stock based on its symbol.
     * VWAP is used to determine the average price a stock has traded at throughout the day, based on both price and volume.
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @return The VWAP as a double.
     * @throws StockNotFoundException if the stock is not found in the repository.
     */
    double calculateVWSP(String symbol) throws StockNotFoundException;
}
