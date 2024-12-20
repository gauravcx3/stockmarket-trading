package com.globalbeverage.stockmarket.trade;

import com.globalbeverage.stockmarket.exception.TradeNotFoundException;
import com.globalbeverage.stockmarket.service.TradeService;
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

/**
 * Unit tests for handling exceptions in the TradeService.
 * This class tests the exception scenarios, specifically for trade retrieval when a trade is not found.
 */
@ExtendWith(MockitoExtension.class)
public class TradeExceptionTest {

    @Mock
    private TradeService tradeService; // Mocked TradeService to simulate service behavior.

    @Mock
    private Logger logger = LoggerFactory.getLogger(TradeService.class); // Mock Logger

    /**
     * Test that a TradeNotFoundException is thrown when a trade is not found by ID.
     * Verifies that the exception message is correct.
     */
    @Test
    void shouldThrowExceptionWhenTradeNotFound() {
        // Arrange: Simulate a scenario where the tradeService throws a TradeNotFoundException.
        when(tradeService.getTradesForStock("Coca Cola")).thenThrow(new TradeNotFoundException("Trade not found: Coca Cola"));

        // Capture log output
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        // Act & Assert: Verify that the TradeNotFoundException is thrown and contains the correct message.
        TradeNotFoundException exception = assertThrows(TradeNotFoundException.class, () -> {
            tradeService.getTradesForStock("Coca Cola"); // Call the method that is expected to throw the exception.
        });

        // Assert: Check if the exception message is as expected.
        assertEquals("Trade not found: Coca Cola", exception.getMessage());

        // Verify that the error was logged
        verify(logger).error(logCaptor.capture());
        assertTrue(logCaptor.getValue().contains("Trade not found: Coca Cola"));
    }
}
