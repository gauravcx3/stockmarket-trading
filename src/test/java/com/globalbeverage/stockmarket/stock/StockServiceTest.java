package com.globalbeverage.stockmarket.stock;

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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void shouldCalculateDividendYieldForCommonStock() {
        // Arrange
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act
        double dividendYield = stockService.calculateDividendYield("Coca Cola", 100);

        // Assert
        assertEquals(1.0, dividendYield, 0.001);
    }

    @Test
    void shouldCalculateDividendYieldForPreferredStock() {
        // Arrange
        Stock stock = new Stock("Pepsi", "PREFERRED", 0, 0.05, 100);
        when(stockRepository.findBySymbol("Pepsi")).thenReturn(Optional.of(stock));

        // Act
        double dividendYield = stockService.calculateDividendYield("Pepsi", 100);

        // Assert
        assertEquals(0.05, dividendYield, 0.001);
    }

    @Test
    void shouldCalculatePERatio() {
        // Arrange
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act
        double peRatio = stockService.calculatePERatio("Coca Cola", 100);

        // Assert
        assertEquals(1.0, peRatio, 0.001);
    }

    @Test
    void shouldCalculateVWSP() {
        // Arrange
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        stock.getTrades().add(new Trade("Coca Cola", null, 10, true, 50));
        stock.getTrades().add(new Trade("Coca Cola", null, 20, true, 60));
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(stock));

        // Act
        double vwsp = stockService.calculateVWSP("Coca Cola");

        // Assert
        assertEquals(57.5, vwsp, 0.01);
    }
}
