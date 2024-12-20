package com.globalbeverage.stockmarket.stock;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.exception.InvalidPriceException;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.service.StockService;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.mockito.ArgumentCaptor;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import java.util.Optional;

@ExtendWith(MockitoExtension.class)
public class StockExceptionTest {

    @Mock
    private StockRepository stockRepository;

    @InjectMocks
    private StockService stockService;

    @Mock
    private Logger logger = LoggerFactory.getLogger(StockService.class);  // Mock the logger

    /**
     * Test case to verify that StockNotFoundException is thrown when the stock is not found.
     */
    @Test
    void shouldThrowStockNotFoundExceptionWhenStockNotFound() {
        // Arrange: Mock the repository to return an empty Optional for a non-existent stock.
        when(stockRepository.findBySymbol("Coca Cola")).thenReturn(Optional.empty());

        // Capture log output
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        // Act & Assert: Verify that StockNotFoundException is thrown with the correct message and log entry.
        StockNotFoundException exception = assertThrows(StockNotFoundException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", 100);
        });

        assertEquals("Stock not found: Coca Cola", exception.getMessage());
        verify(logger).error(logCaptor.capture());  // Capture the log message
        assertTrue(logCaptor.getValue().contains("Stock not found: Coca Cola"));  // Assert the log message contains the expected text
    }

    /**
     * Test case to verify that InvalidPriceException is thrown when an invalid price (<= 0) is provided.
     */
    @Test
    void shouldThrowInvalidPriceExceptionWhenPriceIsInvalid() {
        // Capture log output
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        // Act & Assert: Verify that InvalidPriceException is thrown with the correct message and log entry.
        InvalidPriceException exception = assertThrows(InvalidPriceException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", -50);
        });

        assertEquals("Price must be greater than 0", exception.getMessage());
        verify(logger).error(logCaptor.capture());  // Capture the log message
        assertTrue(logCaptor.getValue().contains("Invalid price: -50 for stock: Coca Cola"));  // Assert the log message contains the expected text
    }

    /**
     * Test case to ensure that a valid price does not throw any exceptions.
     */
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
