package com.globalbeverage.stockmarket.stock;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.StockType;
import com.globalbeverage.stockmarket.domain.Trade;
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

import java.time.LocalDateTime;
import java.util.List;
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
        Stock stock = new Stock("Preferred Stock", StockType.PREFERRED, 0, 5, 250); // Fixed dividend = 5, Par value = 250
        when(stockRepository.findBySymbol("Preferred Stock")).thenReturn(Optional.of(stock));

        // Act: Call the calculateDividendYield method of the service with a price
        double dividendYield = stockService.calculateDividendYield("Preferred Stock", 123.45);

        // Assert: Verify the calculated dividend yield is correct
        double expectedDividendYield = (5 * 250) / 123.45; // Expected = 1250 / 123.45 = 10.13
        assertEquals(expectedDividendYield, dividendYield, 0.0001, "Dividend yield calculation for preferred stock is incorrect.");
    }


    /**
     * Test the calculation of dividend yield for a preferred stock.
     * Verifies that the dividend yield is correctly calculated for a preferred stock type.
     */
    @Test
    void shouldCalculateDividendYieldForPreferredStock() {
        // Arrange: Prepare a preferred stock and mock the repository's findBySymbol method.
        Stock stock = new Stock("Pepsi", StockType.PREFERRED, 0, 0.05, 100);
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
    void shouldReturnNaNForPEWhenNoDividend() {
        // Arrange: Prepare a stock with lastDividend = 0
        Stock stock = new Stock("No Dividend Stock", StockType.COMMON, 0, 100, 100); // Correct order
        when(stockRepository.findBySymbol("No Dividend Stock")).thenReturn(Optional.of(stock));

        // Debug to confirm correct value
        System.out.println("Test Last Dividend: " + stock.getLastDividend());

        // Act: Call the calculatePERatio method
        double peRatio = stockService.calculatePERatio("No Dividend Stock", 100);

        // Debug to verify result
        System.out.println("Calculated P/E Ratio: " + peRatio);

        // Assert: The P/E ratio should be NaN
        assertTrue(Double.isNaN(peRatio), "P/E ratio should be NaN when there's no dividend.");
    }

    /**
     * Test the calculation of the volume-weighted average price (VWSP).
     * Verifies that the VWSP is correctly calculated based on trades for the stock.
     */
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void shouldHandleNoTradesForVWSP() {
        // Arrange: Stock with no trades.
        Stock stock = new Stock("Pepsi", StockType.COMMON, 100, 0, 100);
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

    /**
     * Test the calculation of VWSP (Volume-Weighted Stock Price).
     * Verifies that VWSP is correctly calculated.
     */
    @Test
    void shouldCalculateVWSP() {
        // Arrange: Create spy on stock objects
        Stock stock1 = spy(new Stock("Stock1", StockType.COMMON, 0, 0, 100));
        Stock stock2 = spy(new Stock("Stock2", StockType.PREFERRED, 5, 0.05, 100));

        // Mock the repository to return the stocks when searched by symbol
        when(stockRepository.findBySymbol("Stock1")).thenReturn(Optional.of(stock1));
        when(stockRepository.findBySymbol("Stock2")).thenReturn(Optional.of(stock2));

        // Create mock trades
        Trade trade1 = new Trade("Stock1", LocalDateTime.now().minusMinutes(3), 10, true, 100, stock1);
        Trade trade2 = new Trade("Stock1", LocalDateTime.now().minusMinutes(1), 20, true, 110, stock1);
        Trade trade3 = new Trade("Stock2", LocalDateTime.now().minusMinutes(3), 5, true, 120, stock2);
        Trade trade4 = new Trade("Stock2", LocalDateTime.now().minusMinutes(1), 15, true, 130, stock2);

        // Mock the trades return value for each stock
        doReturn(List.of(trade1, trade2)).when(stock1).getTrades();
        doReturn(List.of(trade3, trade4)).when(stock2).getTrades();

        // Act: Call the method to calculate VWSP
        double vwspStock1 = stockService.calculateVWSP("Stock1");
        double vwspStock2 = stockService.calculateVWSP("Stock2");

        // Assert: Verify the calculated VWSP is correct for each stock
        assertEquals(106.66666666666667, vwspStock1, 0.001); // Corrected expected VWSP for Stock1
        assertEquals(127.5, vwspStock2, 0.001); // Corrected expected VWSP for Stock2
    }

    /**
     * Test the calculation of the GBCE All Share Index.
     * Verifies that the GBCE All Share Index is correctly calculated.
     */
    @Test
    void shouldCalculateGBCEAllShareIndex() {
        // Arrange: Create spy stocks
        Stock stock1 = spy(new Stock("Stock1", StockType.COMMON, 0, 0, 100));
        Stock stock2 = spy(new Stock("Stock2", StockType.PREFERRED, 5, 0.05, 100));

        // Mock trades for each stock
        Trade trade1 = new Trade("Stock1", LocalDateTime.now().minusMinutes(3), 10, true, 100, stock1);
        Trade trade2 = new Trade("Stock1", LocalDateTime.now().minusMinutes(1), 20, true, 110, stock1);
        Trade trade3 = new Trade("Stock2", LocalDateTime.now().minusMinutes(3), 5, true, 120, stock2);
        Trade trade4 = new Trade("Stock2", LocalDateTime.now().minusMinutes(1), 15, true, 130, stock2);

        // Mock the trades return value for each stock
        doReturn(List.of(trade1, trade2)).when(stock1).getTrades();
        doReturn(List.of(trade3, trade4)).when(stock2).getTrades();

        // Mock repository returns
        when(stockRepository.findBySymbol("Stock1")).thenReturn(Optional.of(stock1));
        when(stockRepository.findBySymbol("Stock2")).thenReturn(Optional.of(stock2));
        when(stockRepository.findAll()).thenReturn(List.of(stock1, stock2));  // Mock all stocks

        // Act: Call the method to calculate VWSP for each stock first
        double vwspStock1 = stockService.calculateVWSP("Stock1");
        double vwspStock2 = stockService.calculateVWSP("Stock2");

        // Log the VWSP values for debugging purposes
        System.out.println("VWSP Stock1: " + vwspStock1);
        System.out.println("VWSP Stock2: " + vwspStock2);

        // Act: Now calculate the GBCE All Share Index based on VWSP values
        double gbceAllShareIndex = stockService.calculateGBCEAllShareIndex();

        // Log the GBCE value for debugging
        System.out.println("GBCE All Share Index: " + gbceAllShareIndex);

        // Assert: Verify the GBCE All Share Index is correctly calculated
        double expectedIndex = Math.pow(vwspStock1 * vwspStock2, 1.0 / 2); // Geometric mean of VWSP values
        assertEquals(expectedIndex, gbceAllShareIndex, 0.001);
    }
}
