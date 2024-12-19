package com.globalbeverage.stockmarket;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.repository.StockRepository;
import com.globalbeverage.stockmarket.service.StockService;
import com.globalbeverage.stockmarket.service.StockServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;  // Use StockServiceImpl since it's the concrete implementation

    @Test
    void shouldCalculateDividendYieldForCommonStock() {
        // Arrange: Set up mock for a COMMON stock
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act: Call the service method for dividend yield calculation
        double dividendYield = stockService.calculateDividendYield("Coca Cola", 100);

        // Assert: Verify the expected dividend yield for common stock
        assertEquals(1.0, dividendYield, 0.001);  // lastDividend / price = 100 / 100
    }

    @Test
    void shouldCalculateDividendYieldForPreferredStock() {
        // Arrange: Set up mock for a PREFERRED stock
        Stock stock = new Stock("Pepsi", "PREFERRED", 0, 0.05, 100);
        when(stockRepository.findBySymbol("Pepsi")).thenReturn(Optional.of(stock));

        // Act: Call the service method for dividend yield calculation
        double dividendYield = stockService.calculateDividendYield("Pepsi", 100);

        // Assert: Verify the expected dividend yield for preferred stock
        assertEquals(0.05, dividendYield, 0.001);  // (fixedDividend * parValue) / price = (0.05 * 100) / 100
    }

    @Test
    void shouldThrowExceptionWhenStockNotFoundForDividendCalculation() {
        // Arrange: Set up mock for a stock that's missing from the repository
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.empty());

        // Act & Assert: Verify that an exception is thrown
        Exception exception = assertThrows(RuntimeException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", 100);
        });

        assertEquals("Stock not found", exception.getMessage());
    }

    @Test
    void shouldCalculatePERatio() {
        // Arrange: Set up mock for stock with a known lastDividend
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act: Call the service method for PE ratio calculation
        double peRatio = stockService.calculatePERatio("Coca Cola", 100);

        // Assert: Verify the calculated PE ratio
        assertEquals(1.0, peRatio, 0.001);  // price / lastDividend = 100 / 100
    }

    @Test
    void shouldReturnZeroForPEForNoLastDividend() {
        // Arrange: Set up mock for stock with no last dividend
        Stock stock = new Stock("Coca Cola", "COMMON", 0, 0, 100);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act: Call the service method for PE ratio calculation
        double peRatio = stockService.calculatePERatio("Coca Cola", 100);

        // Assert: PE ratio should be zero if last dividend is zero
        assertEquals(0.0, peRatio, 0.001);
    }

    @Test
    void shouldCalculateVWSP() {
        // Arrange: Create stock and add trades
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 10);  // Constructor now matches
        stock.addTrade(new Trade("Coca Cola", LocalDateTime.now(), 10, true, 50.0));  // Buy 10 at 50.0
        stock.addTrade(new Trade("Coca Cola", LocalDateTime.now(), 20, false, 60.0)); // Sell 20 at 60.0
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act: Calculate VWSP
        double vwsp = stockService.calculateVWSP("Coca Cola");

        // (50*10 + 60*20) / (10+20)
        // Assert: Verify the expected VWSP value
        assertEquals(56.666666666666664, vwsp, 0.01);  // Tolerance of 0.01
    }
}
