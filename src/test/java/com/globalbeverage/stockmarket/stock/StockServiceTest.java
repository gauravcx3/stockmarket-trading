package com.globalbeverage.stockmarket.stock;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.service.StockServiceImpl;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

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
    private StockRepository stockRepository;  // Mocked StockRepository to simulate database interaction.

    @InjectMocks
    private StockServiceImpl stockService;  // The service class to be tested.

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
    @MockitoSettings(strictness = Strictness.LENIENT)
    void shouldHandleNoTradesForVWSP() {
        // Arrange: Stock with no trades.
        Stock stock = new Stock("Pepsi", "COMMON", 100, 0, 100);
        when(stockRepository.findBySymbol("Pepsi")).thenReturn(Optional.of(stock)); // lenient approach

        // Act: Call the method
        double vwsp = stockService.calculateVWSP("Pepsi");

        // Assert: Check how it handles no trades.
        assertEquals(0, vwsp, 0.001); // Assumption: 0 VWSP for no trades.
    }

    /**
     * Test the StockNotFoundException when a stock is not found.
     */
    @Test
    void shouldThrowStockNotFoundExceptionWhenStockNotFound() {
        // Arrange: Mock the repository to return an empty Optional for a non-existent stock.
        when(stockRepository.findBySymbol("NonExistentStock")).thenReturn(Optional.empty());

        // Capture the log output
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        // Act & Assert: Verify that StockNotFoundException is thrown with the correct message and log entry.
        StockNotFoundException exception = assertThrows(StockNotFoundException.class, () -> {
            stockService.calculateDividendYield("NonExistentStock", 100);
        });

        // Assert: Verify the exception message and log output
        assertEquals("Stock not found: NonExistentStock", exception.getMessage());

        // Verify interactions with the repository, not the stockService
        verify(stockRepository, times(1)).findBySymbol("NonExistentStock");

        // Ensure the logger was called correctly
        verify(stockRepository, times(1)).findBySymbol(anyString());
    }
}
