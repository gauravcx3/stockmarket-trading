package com.globalbeverage.stockmarket.trade;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import com.globalbeverage.stockmarket.service.TradeService;
import com.globalbeverage.stockmarket.service.TradeServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TradeServiceTest {

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private TradeServiceImpl tradeService;

    @Test
    void shouldAddTradeSuccessfully() {
        // Arrange
        Trade trade = new Trade("Coca Cola", null, 10, true, 50);
        when(tradeRepository.save(trade)).thenReturn(trade);

        // Act
        Trade savedTrade = tradeService.addTrade(trade);

        // Assert
        assertNotNull(savedTrade);
        assertEquals("Coca Cola", savedTrade.getStockSymbol());
        assertEquals(10, savedTrade.getQuantity());
    }

    @Test
    void shouldThrowExceptionWhenTradeNotFound() {
        // Arrange
        when(tradeRepository.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        Exception exception = assertThrows(RuntimeException.class, () -> {
            tradeService.getTradeById(1L);
        });

        assertEquals("Trade not found: 1", exception.getMessage());
    }
}