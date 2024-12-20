package com.globalbeverage.stockmarket.config;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
    void testValidationWithInvalidData() {
        Stock invalidStock = new Stock("INVALID", "COMMON", -10.0, -5.0, -100.0);

        // Capture logger output
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        // Simulate invalid stock data processing
        try {
            dataLoader.loadData(true);
        } catch (Exception e) {
            // Verify that the logger captured the error message
            verify(logger).error(logCaptor.capture());
            assertTrue(logCaptor.getValue().contains("Invalid stock data"));
        }

        // Assert that an exception is thrown for invalid stock
        Exception exception = assertThrows(Exception.class, () -> {
            dataLoader.loadData(true);
        });

        assertTrue(exception.getMessage().contains("Invalid stock data"));
    }

    /**
     * Test for batch processing of stock data.
     * Verifies that stock data is saved in batches to the repository.
     */
    @Test
    void testBatchProcessing() {
        List<Stock> stocks = List.of(
                new Stock("TEA", "COMMON", 0.0, 0.0, 100.0),
                new Stock("POP", "COMMON", 8.0, 0.0, 100.0),
                new Stock("ALE", "COMMON", 23.0, 0.0, 60.0)
        );

        dataLoader.loadData(true);

        // Verify the repository saved all stocks in batches
        verify(stockRepository, atLeastOnce()).saveAll(stocks);
    }
}
