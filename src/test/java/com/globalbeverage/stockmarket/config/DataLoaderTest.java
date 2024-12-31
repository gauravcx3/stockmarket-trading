package com.globalbeverage.stockmarket.config;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.StockType;
import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.repository.StockRepository;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Unit tests for the DataLoader class.
 * Verifies loading, validation, logging, and batch processing of stock and trade data.
 */
class DataLoaderTest {

    private StockRepository stockRepository;
    private TradeRepository tradeRepository;
    private DataLoader dataLoader;
    private Logger logger;

    @BeforeEach
    void setUp() {
        stockRepository = Mockito.mock(StockRepository.class);
        tradeRepository = Mockito.mock(TradeRepository.class);
        dataLoader = new DataLoader(stockRepository, tradeRepository);
        logger = LoggerFactory.getLogger(DataLoader.class);
    }

    /**
     * Test for loading stock and trade data using preset data.
     * Verifies that stock and trade data is saved to the repository.
     */
    @Test
    void testLoadDataWithPresetData() {
        dataLoader.loadData(true);  // Use preset data
        verify(stockRepository, atLeastOnce()).saveAllStocks(anyList());
        verify(tradeRepository, atLeastOnce()).saveAllTrades(anyList());
    }

    /**
     * Test for loading stock and trade data using randomly generated data.
     * Verifies that stock and trade data is saved to the repository.
     */
    @Test
    void testLoadDataWithRandomData() {
        dataLoader.loadData(false);  // Use randomly generated data
        verify(stockRepository, atLeastOnce()).saveAllStocks(anyList());
        verify(tradeRepository, atLeastOnce()).saveAllTrades(anyList());
    }

    /**
     * Test for validating invalid stock data.
     * Verifies that an exception is thrown and the invalid stock is logged.
     */
    @Test
    void testValidationWithInvalidData() throws Exception {
        Stock invalidStock = new Stock("INVALID", StockType.COMMON, -10.0, -5.0, -100.0);
        List<Stock> stockList = Arrays.asList(invalidStock);

        Method method = DataLoader.class.getDeclaredMethod("validateAndFilterStocks", List.class);
        method.setAccessible(true);

        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        try {
            method.invoke(dataLoader, stockList);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof IllegalArgumentException);
            assertTrue(cause.getMessage().contains("Invalid LastDividend value"));

            verify(logger).error(logCaptor.capture());
            assertTrue(logCaptor.getValue().contains("Invalid stock data"));
        }
    }

    /**
     * Test for validating invalid trade data.
     * Verifies that an exception is thrown and the invalid trade is logged.
     */
    @Test
    void testValidationWithInvalidTradeData() throws Exception {
        Stock stock = new Stock("TEA", StockType.COMMON, 10.0, 0.0, 100.0);
        Trade invalidTrade = new Trade("TEA", LocalDateTime.now(), -5, true, 10.0, stock);  // Invalid quantity
        List<Trade> tradeList = Arrays.asList(invalidTrade);

        Method method = DataLoader.class.getDeclaredMethod("validateAndFilterTrades", List.class);
        method.setAccessible(true);

        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        try {
            method.invoke(dataLoader, tradeList);
        } catch (InvocationTargetException e) {
            Throwable cause = e.getCause();
            assertTrue(cause instanceof IllegalArgumentException);
            assertTrue(cause.getMessage().contains("Quantity must be greater than 0"));

            verify(logger).error(logCaptor.capture());
            assertTrue(logCaptor.getValue().contains("Invalid trade data"));
        }
    }

    /**
     * Test for batch processing of stocks.
     * This test accesses the private saveStocksInBatches method using reflection.
     */
    @Test
    void testBatchProcessingForStocks() throws Exception {
        List<Stock> stocksToSave = Arrays.asList(
                new Stock(null, "TEA", StockType.COMMON, 0.0, 0.0, 100.0),
                new Stock(null, "POP", StockType.COMMON, 8.0, 0.0, 100.0),
                new Stock(null, "ALE", StockType.COMMON, 23.0, 0.0, 60.0),
                new Stock(null, "GIN", StockType.PREFERRED, 8.0, 2.0, 100.0),
                new Stock(null, "JOE", StockType.COMMON, 13.0, 0.0, 250.0)
        );

        Method method = DataLoader.class.getDeclaredMethod("saveStocksInBatches", List.class);
        method.setAccessible(true);

        method.invoke(dataLoader, stocksToSave);

        verify(stockRepository).saveAllStocks(argThat(stocks -> {
            List<Stock> stockList = (List<Stock>) stocks;
            return stockList.size() == 5 &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("TEA")) &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("POP")) &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("ALE")) &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("GIN")) &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("JOE"));
        }));
    }

    /**
     * Test for batch processing of trades.
     * This test accesses the private saveTradesInBatches method using reflection.
     */
    @Test
    void testBatchProcessingForTrades() throws Exception {
        List<Stock> stockList = Arrays.asList(
                new Stock("TEA", StockType.COMMON, 0.0, 0.0, 100.0),
                new Stock("POP", StockType.COMMON, 8.0, 0.0, 100.0)
        );

        List<Trade> tradesToSave = Arrays.asList(
                new Trade("TEA", LocalDateTime.now(), 50, true, 10.5, stockList.get(0)),
                new Trade("POP", LocalDateTime.now(), 100, false, 20.0, stockList.get(1))
        );

        Method method = DataLoader.class.getDeclaredMethod("saveTradesInBatches", List.class);
        method.setAccessible(true);

        method.invoke(dataLoader, tradesToSave);

        verify(tradeRepository).saveAllTrades(argThat(trades -> {
            List<Trade> tradeList = (List<Trade>) trades;
            return tradeList.size() == 2 &&
                    tradeList.stream().anyMatch(trade -> trade.getStock().getSymbol().equals("TEA")) &&
                    tradeList.stream().anyMatch(trade -> trade.getStock().getSymbol().equals("POP"));
        }));
    }
}
