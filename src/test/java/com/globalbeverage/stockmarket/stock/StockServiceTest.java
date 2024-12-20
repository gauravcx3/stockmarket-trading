package com.globalbeverage.stockmarket.stock;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.service.StockServiceImpl;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mockito.ArgumentCaptor;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the StockService implementation.
 * This class tests the methods for calculating dividend yield, P/E ratio, and VWSP for stocks.
 */
@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository; // Mocked StockRepository to simulate database interaction.

    @InjectMocks
    private StockServiceImpl stockService; // The service class to be tested.

    @Mock
    private Logger logger = LoggerFactory.getLogger(StockServiceImpl.class);  // Mock the logger

    /**
     * Test the calculation of dividend yield for a common stock.
     * Verifies that the dividend yield is correctly calculated for a common stock type.
     */
    @Test
    void shouldCalculateDividendYieldForCommonStock() {
        // Arrange: Prepare a common stock and mock the repository's findBySymbol method.
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act: Call the calculateDividendYield method of the service.
        double dividendYield = stockService.calculateDividendYield("Coca Cola", 100);

        // Assert: Verify the calculated dividend yield is correct.
        assertEquals(1.0, dividendYield, 0.001);
    }

    /**
     * Test the calculation of dividend yield for a preferred stock.
     * Verifies that the dividend yield is correctly calculated for a preferred stock type.
     */
    @Test
    void shouldCalculateDividendYieldForPreferredStock() {
        // Arrange: Prepare a preferred stock and mock the repository's findBySymbol method.
        Stock stock = new Stock("Pepsi", "PREFERRED", 0, 0.05, 100);
        when(stockRepository.findBySymbol("Pepsi")).thenReturn(Optional.of(stock));

        // Act: Call the calculateDividendYield method of the service.
        double dividendYield = stockService.calculateDividendYield("Pepsi", 100);

        // Assert: Verify the calculated dividend yield is correct.
        assertEquals(0.05, dividendYield, 0.001);
    }

    /**
     * Test the calculation of the Price-to-Earnings (P/E) ratio.
     * Verifies that the P/E ratio is calculated correctly for a stock.
     */
    @Test
    void shouldCalculatePERatio() {
        // Arrange: Prepare a stock and mock the repository's findBySymbol method.
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act: Call the calculatePERatio method of the service.
        double peRatio = stockService.calculatePERatio("Coca Cola", 100);

        // Assert: Verify the calculated P/E ratio is correct.
        assertEquals(1.0, peRatio, 0.001);
    }

    /**
     * Test the calculation of the volume-weighted average price (VWSP).
     * Verifies that the VWSP is correctly calculated based on trades for the stock.
     */
    @Test
    void shouldCalculateVWSP() {
        // Arrange: Prepare a stock with trades and mock the repository's findBySymbol method.
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        stock.getTrades().add(new Trade("Coca Cola", null, 10, true, 50)); // 10 shares at $50
        stock.getTrades().add(new Trade("Coca Cola", null, 20, true, 60)); // 20 shares at $60
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act: Call the calculateVWSP method of the service.
        double vwsp = stockService.calculateVWSP("Coca Cola");

        // Assert: Verify the calculated VWSP is correct.
        assertEquals(57.5, vwsp, 0.01); // Expected VWSP = (10*50 + 20*60) / (10 + 20) = 57.5
    }

    /**
     * Test the StockNotFoundException when a stock is not found.
     */
    @Test
    void shouldThrowStockNotFoundExceptionWhenStockNotFound() {
        // Arrange: Mock the repository to return an empty Optional for a non-existent stock.
        when(stockRepository.findBySymbol("NonExistentStock")).thenReturn(Optional.empty());

        // Capture log output
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        // Act & Assert: Verify that StockNotFoundException is thrown with the correct message and log entry.
        StockNotFoundException exception = assertThrows(StockNotFoundException.class, () -> {
            stockService.calculateDividendYield("NonExistentStock", 100);
        });

        assertEquals("Stock not found: NonExistentStock", exception.getMessage());
        verify(logger).error(logCaptor.capture());  // Capture the log message
        assertTrue(logCaptor.getValue().contains("Stock not found: NonExistentStock"));
    }
}
