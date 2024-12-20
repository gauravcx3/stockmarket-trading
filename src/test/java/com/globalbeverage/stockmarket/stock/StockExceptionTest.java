package com.globalbeverage.stockmarket.stock;

import com.globalbeverage.stockmarket.exception.StockExceptions.*;
import com.globalbeverage.stockmarket.service.StockService;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StockExceptionTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @Test
    void shouldThrowExceptionWhenStockNotFound() {
        // Arrange
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(StockNotFoundException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", 100);
        });

        assertEquals("Stock not found: Coca Cola", exception.getMessage());
    }

    @Test
    void shouldThrowExceptionForInvalidPrice() {
        // Act & Assert
        Exception exception = assertThrows(InvalidPriceException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", -50);
        });

        assertEquals("Price must be greater than 0", exception.getMessage());
    }
}
