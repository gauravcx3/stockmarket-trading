package com.globalbeverage.stockmarket.stock;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.exception.InvalidPriceException;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.StockRepository;
import com.globalbeverage.stockmarket.service.StockServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StockExceptionTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    @Test
    void shouldThrowStockNotFoundExceptionWhenStockNotFound() {
        // Arrange: Mock the repository to return an empty Optional for a non-existent stock.
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.empty());

        // Act & Assert: Verify that StockNotFoundException is thrown with the correct message.
        StockNotFoundException exception = assertThrows(StockNotFoundException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", 100);
        });

        // Assert the exception message
        assertEquals("Stock not found: Coca Cola", exception.getMessage());
    }

    @Test
    void shouldThrowInvalidPriceExceptionWhenPriceIsInvalid() {
        // Arrange: Mock the repository to return a valid stock (even though the price will be invalid)
        Stock mockStock = new Stock("Coca Cola", "COMMON", 10.0, 0.0, 100.0);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(mockStock));

        // Act & Assert: Verify that InvalidPriceException is thrown when price is invalid (negative in this case)
        InvalidPriceException exception = assertThrows(InvalidPriceException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", -50);
        });

        // Assert the exception message
        assertEquals("The price provided is invalid. It must be greater than zero.", exception.getMessage());
    }

    @Test
    void shouldNotThrowExceptionForValidPrice() {
        // Arrange: Mock stock data
        Stock mockStock = new Stock("Coca Cola", "COMMON", 10.0, 0.0, 100.0);
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.of(mockStock));

        // Act & Assert: Ensure that no exception is thrown when a valid price is provided.
        assertDoesNotThrow(() -> {
            stockService.calculateDividendYield("Coca Cola", 100);
        });
    }
}
