package com.globalbeverage.stockmarket.trade;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.StockType;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import com.globalbeverage.stockmarket.service.TradeServiceImpl;
import jakarta.validation.ConstraintViolationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the TradeService implementation.
 * This class tests the behavior of methods in TradeService, such as recording trades and retrieving trades for a stock.
 */
@ExtendWith(MockitoExtension.class)
public class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeServiceImpl tradeService;

    /**
     * Test the successful recording of a trade.
     * Verifies that the trade is correctly validated and saved.
     */
    @Test
    void shouldRecordTradeSuccessfully() {
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", StockType.COMMON, 100, 0, 100);
        Trade trade = new Trade("Coca Cola", timestamp, 10, true, 50, stock);

        when(tradeRepository.save(trade)).thenReturn(trade);

        tradeService.recordTrade(trade);

        verify(tradeRepository, times(1)).save(trade);
    }

    /**
     * Test that an exception is thrown when trying to record a trade with an invalid price.
     * Verifies that the correct exception is thrown.
     */
    @Test
    void shouldThrowExceptionWhenInvalidTradePrice() {
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", StockType.COMMON, 100, 0, 100);
        Trade trade = new Trade("Coca Cola", timestamp, 10, true, -50, stock);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            tradeService.recordTrade(trade);
        });

        assertTrue(exception.getConstraintViolations().stream()
                .anyMatch(violation -> violation.getMessage().contains("Price must be non-negative")));
    }

    /**
     * Test that the service correctly retrieves a list of trades for a specific stock.
     * Verifies that the trades are fetched from the repository.
     */
    @Test
    void shouldReturnListOfTradesForStock() {
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", StockType.COMMON, 100, 0, 100);
        Trade trade1 = new Trade("Coca Cola", timestamp, 10, true, 50, stock);
        Trade trade2 = new Trade("Coca Cola", timestamp, 20, false, 60, stock);
        List<Trade> trades = List.of(trade1, trade2);
        when(tradeRepository.findByStockSymbol("Coca Cola")).thenReturn(trades);

        List<Trade> fetchedTrades = tradeService.getTradesForStock("Coca Cola");

        assertNotNull(fetchedTrades);
        assertEquals(2, fetchedTrades.size());
        assertEquals("Coca Cola", fetchedTrades.get(0).getStockSymbol());
    }

    /**
     * Test that the service returns an empty list when no trades are found for a stock.
     */
    @Test
    void shouldReturnEmptyListWhenNoTradesFound() {
        when(tradeRepository.findByStockSymbol("Coca Cola")).thenReturn(List.of());

        List<Trade> fetchedTrades = tradeService.getTradesForStock("Coca Cola");

        assertNotNull(fetchedTrades);
        assertTrue(fetchedTrades.isEmpty());
    }

    /**
     * Test that an exception is thrown when trying to record a trade with an invalid quantity.
     * Verifies that the correct exception is thrown.
     */
    @Test
    void shouldThrowExceptionWhenInvalidTradeQuantity() {
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", StockType.COMMON, 100, 0, 100);
        Trade trade = new Trade("Coca Cola", timestamp, 0, true, 50.0, stock);

        ConstraintViolationException exception = assertThrows(ConstraintViolationException.class, () -> {
            tradeService.recordTrade(trade);
        });

        assertTrue(exception.getConstraintViolations().stream()
                .anyMatch(violation -> violation.getMessage().equals("Quantity must be greater than 0")));
    }

    /**
     * Test that the repository's findByStockSymbol method is called correctly.
     */
    @Test
    void shouldCallFindByStockSymbol() {
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", StockType.COMMON, 100, 0, 100);
        Trade trade = new Trade("Coca Cola", timestamp, 10, true, 50, stock);
        when(tradeRepository.findByStockSymbol("Coca Cola")).thenReturn(List.of(trade));

        tradeService.getTradesForStock("Coca Cola");

        verify(tradeRepository, times(1)).findByStockSymbol("Coca Cola");
    }

    /**
     * Test that an empty list is returned when no trades are found for a stock symbol.
     */
    @Test
    void shouldReturnEmptyListWhenStockSymbolNotFound() {
        when(tradeRepository.findByStockSymbol("NonExistentStock")).thenReturn(null);

        List<Trade> fetchedTrades = tradeService.getTradesForStock("NonExistentStock");

        assertNotNull(fetchedTrades);
        assertTrue(fetchedTrades.isEmpty());
    }
}
