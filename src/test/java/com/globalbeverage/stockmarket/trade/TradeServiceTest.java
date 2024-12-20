package com.globalbeverage.stockmarket.trade;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.domain.Stock; // Assuming Stock class exists
import com.globalbeverage.stockmarket.repository.TradeRepository;
import com.globalbeverage.stockmarket.service.TradeServiceImpl;
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
    private TradeRepository tradeRepository; // Mocked TradeRepository to simulate database interaction.

    @InjectMocks
    private TradeServiceImpl tradeService; // The service class to be tested.

    /**
     * Test the successful recording of a trade.
     * Verifies that the trade is correctly validated and saved.
     */
    @Test
    void shouldRecordTradeSuccessfully() {
        // Arrange: Prepare a trade object with a valid timestamp and mock the repository's save method.
        LocalDateTime timestamp = LocalDateTime.now(); // Use current date and time for timestamp
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100); // Create or mock a Stock object
        Trade trade = new Trade("Coca Cola", timestamp, 10, true, 50, stock); // Provide the stock object and timestamp

        when(tradeRepository.save(trade)).thenReturn(trade);

        // Act: Call the recordTrade method of the service.
        tradeService.recordTrade(trade);

        // Assert: Verify that the repository's save method was called with the correct trade.
        verify(tradeRepository, times(1)).save(trade);
    }

    /**
     * Test that an exception is thrown when trying to record a trade with an invalid price.
     * Verifies that the correct exception is thrown.
     */
    @Test
    void shouldThrowExceptionWhenInvalidTradePrice() {
        // Arrange: Prepare a trade object with an invalid price (<= 0).
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        Trade trade = new Trade("Coca Cola", timestamp, 10, true, -50, stock);

        // Act & Assert: Verify that an IllegalArgumentException is thrown.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tradeService.recordTrade(trade);
        });

        // Assert: Verify the exception message.
        assertEquals("Trade price must be greater than 0", exception.getMessage());
    }

    /**
     * Test that the service correctly retrieves a list of trades for a specific stock.
     * Verifies that the trades are fetched from the repository.
     */
    @Test
    void shouldReturnListOfTradesForStock() {
        // Arrange: Prepare a list of trades for a stock and mock the repository method.
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        Trade trade1 = new Trade("Coca Cola", timestamp, 10, true, 50, stock);
        Trade trade2 = new Trade("Coca Cola", timestamp, 20, false, 60, stock);
        List<Trade> trades = List.of(trade1, trade2);
        when(tradeRepository.findByStockSymbol("Coca Cola")).thenReturn(trades);

        // Act: Call the getTradesForStock method.
        List<Trade> fetchedTrades = tradeService.getTradesForStock("Coca Cola");

        // Assert: Verify that the list of trades is returned and contains the correct number of trades.
        assertNotNull(fetchedTrades);
        assertEquals(2, fetchedTrades.size());
        assertEquals("Coca Cola", fetchedTrades.get(0).getStockSymbol());
    }

    /**
     * Test that the service returns an empty list when no trades are found for a stock.
     */
    @Test
    void shouldReturnEmptyListWhenNoTradesFound() {
        // Arrange: Mock the repository to return an empty list for a stock.
        when(tradeRepository.findByStockSymbol("Coca Cola")).thenReturn(List.of());

        // Act: Call the getTradesForStock method.
        List<Trade> fetchedTrades = tradeService.getTradesForStock("Coca Cola");

        // Assert: Verify that the list is empty.
        assertNotNull(fetchedTrades);
        assertTrue(fetchedTrades.isEmpty());
    }

    /**
     * Test that an exception is thrown when trying to record a trade with an invalid quantity.
     * Verifies that the correct exception is thrown.
     */
    @Test
    void shouldThrowExceptionWhenInvalidTradeQuantity() {
        // Arrange: Prepare a trade object with an invalid quantity (<= 0).
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        Trade trade = new Trade("Coca Cola", timestamp, 0, true, 50, stock);

        // Act & Assert: Verify that an IllegalArgumentException is thrown.
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            tradeService.recordTrade(trade);
        });

        // Assert: Verify the exception message.
        assertEquals("Trade quantity must be greater than 0", exception.getMessage());
    }

    /**
     * Test that the repository's findByStockSymbol method is called correctly.
     */
    @Test
    void shouldCallFindByStockSymbol() {
        // Arrange: Prepare a trade and mock the repository's findByStockSymbol method.
        LocalDateTime timestamp = LocalDateTime.now();
        Stock stock = new Stock("Coca Cola", "COMMON", 100, 0, 100);
        Trade trade = new Trade("Coca Cola", timestamp, 10, true, 50, stock);
        when(tradeRepository.findByStockSymbol("Coca Cola")).thenReturn(List.of(trade));

        // Act: Call the getTradesForStock method.
        tradeService.getTradesForStock("Coca Cola");

        // Assert: Verify that the repository's findByStockSymbol method was called.
        verify(tradeRepository, times(1)).findByStockSymbol("Coca Cola");
    }

    /**
     * Test that an exception is thrown when the repository returns null for the stock symbol.
     */
    @Test
    void shouldThrowExceptionWhenStockSymbolNotFound() {
        // Arrange: Mock the repository to return null for a non-existent stock.
        when(tradeRepository.findByStockSymbol("NonExistentStock")).thenReturn(null);

        // Act & Assert: Verify that an IllegalStateException is thrown.
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            tradeService.getTradesForStock("NonExistentStock");
        });

        // Assert: Verify the exception message.
        assertEquals("No trades found for stock symbol: NonExistentStock", exception.getMessage());
    }
}
