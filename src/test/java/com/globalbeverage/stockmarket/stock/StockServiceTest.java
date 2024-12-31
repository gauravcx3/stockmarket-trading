package com.globalbeverage.stockmarket.stock;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.StockType;
import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import com.globalbeverage.stockmarket.service.impl.StockServiceImpl;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for the StockService implementation.
 * This class tests the methods for calculating dividend yield, P/E ratio, and VWSP for stocks.
 */
@ExtendWith(MockitoExtension.class)
public class StockServiceTest {

    @Mock
    private StockRepository stockRepository;

    @Mock
    private TradeRepository tradeRepository;

    @InjectMocks
    private StockServiceImpl stockService;

    /**
     * Test the calculation of dividend yield for a common stock.
     * Verifies that the dividend yield is correctly calculated for a common stock type.
     */
    @Test
    void shouldCalculateDividendYieldForCommonStock() {
        Stock stock = new Stock("Preferred Stock", StockType.PREFERRED, 0, 5, 250);
        when(stockRepository.findStockBySymbol("Preferred Stock")).thenReturn(Optional.of(stock));
        double dividendYield = stockService.calculateDividendYield("Preferred Stock", 123.45);
        double expectedDividendYield = (5 * 250) / 123.45;
        assertEquals(expectedDividendYield, dividendYield, 0.0001, "Dividend yield calculation for preferred stock is incorrect.");
    }

    /**
     * Test the calculation of dividend yield for a preferred stock.
     * Verifies that the dividend yield is correctly calculated for a preferred stock type.
     */
    @Test
    void shouldCalculateDividendYieldForPreferredStock() {
        Stock stock = new Stock("Pepsi", StockType.PREFERRED, 0, 0.05, 100);
        when(stockRepository.findStockBySymbol("Pepsi")).thenReturn(Optional.of(stock));
        double dividendYield = stockService.calculateDividendYield("Pepsi", 100);
        assertEquals(0.05, dividendYield, 0.001);
    }

    /**
     * Test the calculation of the Price-to-Earnings (P/E) ratio.
     * Verifies that the P/E ratio is calculated correctly for a stock.
     */
    @Test
    void shouldReturnNaNForPEWhenNoDividend() {
        Stock stock = new Stock("No Dividend Stock", StockType.COMMON, 0, 100, 100);
        when(stockRepository.findStockBySymbol("No Dividend Stock")).thenReturn(Optional.of(stock));
        double peRatio = stockService.calculatePERatio("No Dividend Stock", 100);
        assertTrue(Double.isNaN(peRatio), "P/E ratio should be NaN when there's no dividend.");
    }

    /**
     * Test the calculation of the volume-weighted average price (VWSP).
     * Verifies that the VWSP is correctly calculated based on trades for the stock,
     * and that the VWSP is 0 when there are no trades for the stock.
     */
    @Test
    @MockitoSettings(strictness = Strictness.LENIENT)
    void shouldHandleNoTradesForVWSP() {
        Stock stock = new Stock("Pepsi", StockType.COMMON, 100, 0, 100);
        when(stockRepository.findStockBySymbol("Pepsi")).thenReturn(Optional.of(stock));

        when(tradeRepository.findTradesBySymbolAndTimestampBetween(
                eq("Pepsi"), any(), any()))
                .thenReturn(Collections.emptyList());

        double vwsp = stockService.calculateVWSP("Pepsi");

        assertEquals(0, vwsp, 0.001);
    }

    /**
     * Test the StockNotFoundException when a stock is not found.
     */
    @Test
    void shouldThrowStockNotFoundExceptionWhenStockNotFound() {
        when(stockRepository.findStockBySymbol("NonExistentStock")).thenReturn(Optional.empty());
        ArgumentCaptor<String> logCaptor = ArgumentCaptor.forClass(String.class);
        StockNotFoundException exception = assertThrows(StockNotFoundException.class, () -> {
            stockService.calculateDividendYield("NonExistentStock", 100);
        });
        assertEquals("Stock not found: NonExistentStock", exception.getMessage());
        verify(stockRepository, times(1)).findStockBySymbol("NonExistentStock");
    }

    /**
     * Test the calculation of VWSP (Volume-Weighted Stock Price).
     * Verifies that VWSP is correctly calculated based on trades for the stock.
     */
    @Test
    void shouldCalculateVWSP() {
        Stock stock1 = new Stock("Stock1", StockType.COMMON, 0, 0, 100);
        Stock stock2 = new Stock("Stock2", StockType.PREFERRED, 5, 0.05, 100);

        when(stockRepository.findStockBySymbol("Stock1")).thenReturn(Optional.of(stock1));
        when(stockRepository.findStockBySymbol("Stock2")).thenReturn(Optional.of(stock2));

        LocalDateTime now = LocalDateTime.now();
        Trade trade1 = new Trade("Stock1", now.minusMinutes(3), 10, true, 100, stock1);
        Trade trade2 = new Trade("Stock1", now.minusMinutes(1), 20, true, 110, stock1);
        Trade trade3 = new Trade("Stock2", now.minusMinutes(3), 5, true, 120, stock2);
        Trade trade4 = new Trade("Stock2", now.minusMinutes(1), 15, true, 130, stock2);

        when(tradeRepository.findTradesBySymbolAndTimestampBetween(
                eq("Stock1"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(trade1, trade2));

        when(tradeRepository.findTradesBySymbolAndTimestampBetween(
                eq("Stock2"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(trade3, trade4));

        double vwspStock1 = stockService.calculateVWSP("Stock1");
        double vwspStock2 = stockService.calculateVWSP("Stock2");

        assertEquals(106.66666666666667, vwspStock1, 0.001);
        assertEquals(127.5, vwspStock2, 0.001);
    }

    /**
     * Test the calculation of the GBCE All Share Index.
     * Verifies that the GBCE All Share Index is correctly calculated.
     */
    @Test
    void shouldCalculateGBCEAllShareIndex() {
        Stock stock1 = new Stock("Stock1", StockType.COMMON, 0, 0, 100);
        Stock stock2 = new Stock("Stock2", StockType.PREFERRED, 5, 0.05, 100);

        when(stockRepository.findStockBySymbol("Stock1")).thenReturn(Optional.of(stock1));
        when(stockRepository.findStockBySymbol("Stock2")).thenReturn(Optional.of(stock2));
        when(stockRepository.findAllStocks()).thenReturn(List.of(stock1, stock2));

        LocalDateTime now = LocalDateTime.now();
        Trade trade1 = new Trade("Stock1", now.minusMinutes(3), 10, true, 100, stock1);
        Trade trade2 = new Trade("Stock1", now.minusMinutes(1), 20, true, 110, stock1);
        Trade trade3 = new Trade("Stock2", now.minusMinutes(3), 5, true, 120, stock2);
        Trade trade4 = new Trade("Stock2", now.minusMinutes(1), 15, true, 130, stock2);

        when(tradeRepository.findTradesBySymbolAndTimestampBetween(
                eq("Stock1"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(trade1, trade2));

        when(tradeRepository.findTradesBySymbolAndTimestampBetween(
                eq("Stock2"), any(LocalDateTime.class), any(LocalDateTime.class)))
                .thenReturn(List.of(trade3, trade4));

        // Calculate VWSP for both stocks
        double vwspStock1 = stockService.calculateVWSP("Stock1");
        double vwspStock2 = stockService.calculateVWSP("Stock2");

        // Calculate the GBCE All Share Index
        double gbceAllShareIndex = stockService.calculateGBCEAllShareIndex();

        // Calculate expected index based on VWSPs
        double expectedIndex = Math.pow(vwspStock1 * vwspStock2, 1.0 / 2);
        assertEquals(expectedIndex, gbceAllShareIndex, 0.001);
    }
}
