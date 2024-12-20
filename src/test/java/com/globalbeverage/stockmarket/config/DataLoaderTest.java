package com.globalbeverage.stockmarket.config;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.StockType;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the DataLoader class.
 * Verifies loading, validation, logging, and batch processing of stock data.
 */
class DataLoaderTest {

    private StockRepository stockRepository;
    private DataLoader dataLoader;
    private Logger logger;

    @BeforeEach
    void setUp() {
        stockRepository = Mockito.mock(StockRepository.class);
        dataLoader = new DataLoader(stockRepository);
        logger = LoggerFactory.getLogger(DataLoader.class);
    }

    /**
     * Test for loading data using preset stock data.
     * Verifies that preset data is saved to the repository.
     */
    @Test
    void testLoadDataWithPresetData() {
        dataLoader.loadData(true);

        // Verify that pre-set data was saved to the repository
        verify(stockRepository, atLeastOnce()).saveAll(anyList());
    }

    /**
     * Test for loading data using randomly generated stock data.
     * Verifies that random data is saved to the repository.
     */
    @Test
    void testLoadDataWithRandomData() {
        dataLoader.loadData(false);

        // Verify that random data was saved to the repository
        verify(stockRepository, atLeastOnce()).saveAll(anyList());
    }

    /**
     * Test for validating invalid stock data.
     * Verifies that an exception is thrown and the invalid stock is logged.
     */
    @Test
    void testValidationWithInvalidData() throws Exception {
        Stock invalidStock = new Stock("INVALID", StockType.COMMON, -10.0, -5.0, -100.0);
        List<Stock> stockList = Arrays.asList(invalidStock);

        // Use reflection to access the private validateAndFilterStocks method
        Method method = DataLoader.class.getDeclaredMethod("validateAndFilterStocks", List.class);
        method.setAccessible(true);

        // Capture logger output
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        // Invoke the method using reflection and assert the exception is thrown
        try {
            method.invoke(dataLoader, stockList);
        } catch (InvocationTargetException e) {
            // This catches the IllegalArgumentException thrown by validateData
            Throwable cause = e.getCause();
            assertTrue(cause instanceof IllegalArgumentException);
            assertTrue(cause.getMessage().contains("Invalid LastDividend value"));

            // Verify the logger captured the error message
            verify(logger).error(logCaptor.capture());
            assertTrue(logCaptor.getValue().contains("Invalid stock data"));
        }
    }

    /**
     * Test the batch processing of stocks.
     * This test accesses the private saveStocksInBatches method using reflection.
     */
    @Test
    void testBatchProcessing() throws Exception {
        // Arrange: Prepare a list of 5 stocks to be saved in batches.
        List<Stock> stocksToSave = Arrays.asList(
                new Stock(null, "TEA", StockType.COMMON, 0.0, 0.0, 100.0),
                new Stock(null, "POP", StockType.COMMON, 8.0, 0.0, 100.0),
                new Stock(null, "ALE", StockType.COMMON, 23.0, 0.0, 60.0),
                new Stock(null, "GIN", StockType.PREFERRED, 8.0, 2.0, 100.0),
                new Stock(null, "JOE", StockType.COMMON, 13.0, 0.0, 250.0)
        );

        // Use reflection to access the private saveStocksInBatches method
        Method method = DataLoader.class.getDeclaredMethod("saveStocksInBatches", List.class);
        method.setAccessible(true);  // Make the private method accessible

        // Act: Invoke the method using reflection
        method.invoke(dataLoader, stocksToSave);

        // Verify: Ensure the saveAll method was called with the correct 5 stocks
        verify(stockRepository).saveAll(argThat(stocks -> {
            // Convert the Iterable to a List and check its size
            List<Stock> stockList = (List<Stock>) stocks;
            return stockList.size() == 5 &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("TEA")) &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("POP")) &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("ALE")) &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("GIN")) &&
                    stockList.stream().anyMatch(stock -> stock.getSymbol().equals("JOE"));
        }));
    }
}
