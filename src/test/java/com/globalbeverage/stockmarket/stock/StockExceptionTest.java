package com.globalbeverage.stockmarket.stock;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.StockType;
import com.globalbeverage.stockmarket.exception.InvalidPriceException;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.StockRepository;
import com.globalbeverage.stockmarket.service.impl.StockServiceImpl;
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

    /**
     * Verifies that StockNotFoundException is thrown when a stock is not found in the repository.
     * @throws StockNotFoundException if the stock is not found.
     */
    @Test
    void shouldThrowStockNotFoundExceptionWhenStockNotFound() {
        when(stockRepository.findStockBySymbol("Coca Cola")).thenReturn(Optional.empty());

        StockNotFoundException exception = assertThrows(StockNotFoundException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", 100);
        });

        assertEquals("Stock not found: Coca Cola", exception.getMessage());
    }

    /**
     * Verifies that InvalidPriceException is thrown when the price provided is invalid (negative value).
     * @throws InvalidPriceException if the price is invalid.
     */
    @Test
    void shouldThrowInvalidPriceExceptionWhenPriceIsInvalid() {
        Stock mockStock = new Stock("Coca Cola", StockType.COMMON, 10.0, 0.0, 100.0);
        when(stockRepository.findStockBySymbol("Coca Cola")).thenReturn(Optional.of(mockStock));

        InvalidPriceException exception = assertThrows(InvalidPriceException.class, () -> {
            stockService.calculateDividendYield("Coca Cola", -50);
        });

        assertEquals("The price provided is invalid. It must be greater than zero.", exception.getMessage());
    }

    /**
     * Verifies that no exception is thrown when a valid price is provided.
     */
    @Test
    void shouldNotThrowExceptionForValidPrice() {
        Stock mockStock = new Stock("Coca Cola", StockType.COMMON, 10.0, 0.0, 100.0);
        when(stockRepository.findStockBySymbol("Coca Cola")).thenReturn(Optional.of(mockStock));

        assertDoesNotThrow(() -> {
            stockService.calculateDividendYield("Coca Cola", 100);
        });
    }
}
