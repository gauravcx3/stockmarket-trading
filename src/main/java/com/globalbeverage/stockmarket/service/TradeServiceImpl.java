package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.exception.StockNotFoundException;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Validation;
import jakarta.validation.Validator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
                    .map(violation -> violation.getMessage())
                    .collect(Collectors.joining(", "));

            logger.error("Invalid trade: {} - Violations found: {}", trade, violationMessages);
            throw new ConstraintViolationException("Trade validation failed", violations);
        }

        if (trade.isBuy() && trade.getPrice() <= 0) {
            throw new IllegalArgumentException("Price must be greater than 0 for buy trades");
        }

        tradeRepository.save(trade);
        logger.info("Trade recorded: {} - Price: {} Quantity: {}", trade.getStockSymbol(), trade.getPrice(), trade.getQuantity());
    }

    /**
     * Retrieves trades for a specific stock.
     * Throws an exception if no trades are found.
     *
     * @param stockSymbol The stock symbol.
     * @return List of trades.
     * @throws StockNotFoundException if no trades exist for the stock.
     */
    @Override
    public List<Trade> getTradesForStock(String stockSymbol) {
        List<Trade> trades = tradeRepository.findByStockSymbol(stockSymbol);
        if (trades == null || trades.isEmpty()) {
            return new ArrayList<>();
        }
        return trades;
    }

    /**
     * Retrieves trades for a specific stock, returning an empty list if none exist.
     *
     * @param stockSymbol The stock symbol.
     * @return List of trades or an empty list if none found.
     */
    @Override
    public Optional<List<Trade>> getTradesForStockOptional(String stockSymbol) {
        List<Trade> trades = tradeRepository.findByStockSymbol(stockSymbol);
        if (trades.isEmpty()) {
            logger.warn("No trades found for stock: {}", stockSymbol);
            return Optional.empty();
        }
        logger.info("Retrieved {} trades for stock: {}", trades.size(), stockSymbol);
        return Optional.of(trades);
    }
}
