package com.globalbeverage.stockmarket.trade;

import com.globalbeverage.stockmarket.exception.TradeNotFoundException;
import com.globalbeverage.stockmarket.service.TradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
    private TradeService tradeService;

    private static final Logger logger = LoggerFactory.getLogger(TradeService.class);

    /**
     * Test that a TradeNotFoundException is thrown when a trade is not found by ID.
     * Verifies that the exception message is correct.
     */
    @Test
    void shouldThrowExceptionWhenTradeNotFound() {
        when(tradeService.getTradesForStock("Coca Cola")).thenThrow(new TradeNotFoundException("Trade not found: Coca Cola"));

        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);

        TradeNotFoundException exception = assertThrows(TradeNotFoundException.class, () -> {
            tradeService.getTradesForStock("Coca Cola");
        });

        assertEquals("Trade not found: Coca Cola", exception.getMessage());
        verify(tradeService, times(1)).getTradesForStock("Coca Cola");
    }
}
