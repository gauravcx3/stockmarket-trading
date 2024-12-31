package com.globalbeverage.stockmarket.service.impl;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import com.globalbeverage.stockmarket.service.TradeService;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of TradeService, handling trade recording and retrieval.
 */
@Service
public class TradeServiceImpl implements TradeService {

    private static final Logger logger = LoggerFactory.getLogger(TradeServiceImpl.class);

    @Autowired
    private TradeRepository tradeRepository;

    private static final Validator validator = Validation.buildDefaultValidatorFactory().getValidator();

    /**
     * Records a new trade after validating the price and quantity.
     *
     * @param trade The trade to be recorded.
     * @throws ConstraintViolationException if validation fails.
     */
    @Override
    public void recordTrade(Trade trade) {
        Set<ConstraintViolation<Trade>> violations = validator.validate(trade);
        if (!violations.isEmpty()) {
            String violationMessages = violations.stream()
                    .map(ConstraintViolation::getMessage)
                    .collect(Collectors.joining(", "));
            logger.error("Invalid trade: {} - Violations found: {}", trade, violationMessages);
            throw new ConstraintViolationException("Trade validation failed", violations);
        }

        if (trade.isBuy() && trade.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0 for buy trades.");
        }

        tradeRepository.saveTrade(trade);
        logger.info("Trade recorded: {} - Price: {} Quantity: {}", trade.getStockSymbol(), trade.getPrice(), trade.getQuantity());
    }

    /**
     * Retrieves trades for a specific stock.
     *
     * @param stockSymbol The stock symbol.
     * @return List of trades for the stock.
     */
    @Override
    public List<Trade> getTradesForStock(String stockSymbol) {
        List<Trade> trades = tradeRepository.findTradesBySymbol(stockSymbol);
        if (trades == null || trades.isEmpty()) {
            logger.warn("No trades found for stock: {}", stockSymbol);
            return new ArrayList<>();
        }
        logger.info("Retrieved {} trades for stock: {}", trades.size(), stockSymbol);
        return trades;
    }

    /**
     * Retrieves trades for a specific stock within a time range.
     *
     * @param stockSymbol The stock symbol.
     * @param start       The start of the time range.
     * @param end         The end of the time range.
     * @return List of trades within the time range.
     */
    public List<Trade> getTradesForStockWithinTimeRange(String stockSymbol, LocalDateTime start, LocalDateTime end) {
        List<Trade> trades = tradeRepository.findTradesBySymbolAndTimestampBetween(stockSymbol, start, end);
        if (trades.isEmpty()) {
            logger.warn("No trades found for stock: {} within the time range {} to {}", stockSymbol, start, end);
            return new ArrayList<>();
        }
        logger.info("Retrieved {} trades for stock: {} within the time range {} to {}", trades.size(), stockSymbol, start, end);
        return trades;
    }

    /**
     * Retrieves trades for a specific stock, returning an optional.
     *
     * @param stockSymbol The stock symbol.
     * @return Optional containing a list of trades or empty if none found.
     */
    @Override
    public Optional<List<Trade>> getTradesForStockOptional(String stockSymbol) {
        List<Trade> trades = tradeRepository.findTradesBySymbol(stockSymbol);
        if (trades.isEmpty()) {
            logger.warn("No trades found for stock: {}", stockSymbol);
            return Optional.empty();
        }
        logger.info("Retrieved {} trades for stock: {}", trades.size(), stockSymbol);
        return Optional.of(trades);
    }
}
