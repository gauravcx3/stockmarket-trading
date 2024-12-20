package com.globalbeverage.stockmarket.trade;

import com.globalbeverage.stockmarket.exception.TradeExceptions.*;
import com.globalbeverage.stockmarket.service.TradeService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradeExceptionTest {

    @Mock
    private TradeService tradeService;

    @Test
    void shouldThrowExceptionWhenTradeNotFound() {
        // Arrange
        when(tradeService.getTradeById(1L)).thenThrow(new TradeNotFoundException("Trade not found: 1"));

        // Act & Assert
        Exception exception = assertThrows(TradeNotFoundException.class, () -> {
            tradeService.getTradeById(1L);
        });

        assertEquals("Trade not found: 1", exception.getMessage());
    }
}
