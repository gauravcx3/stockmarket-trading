package com.globalbeverage.stockmarket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Represents a stock in the stock market.
 * A stock can be of type "COMMON" or "PREFERRED" and has financial attributes such as dividends and par value.
 */
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Stock {

    private static final Logger logger = LoggerFactory.getLogger(Stock.class); // Logger for Stock class

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // Unique identifier for the stock

    @NotNull(message = "Symbol cannot be null") // Ensures the symbol is provided
    private String symbol;          // Stock symbol (e.g., TEA, POP)

    @NotNull(message = "Stock type cannot be null") // Ensures the stock type is provided
    private String type;            // Type of the stock ("COMMON" or "PREFERRED")

    @Min(value = 0, message = "Last dividend must be non-negative") // Ensures lastDividend is non-negative
    private double lastDividend;    // Last dividend in pennies (for COMMON)

    private double fixedDividend;   // Fixed dividend (only applicable for PREFERRED)

    @Min(value = 0, message = "Par value must be non-negative") // Ensures parValue is non-negative
    private double parValue;        // Par value (e.g., 100.0)

    private final List<Trade> trades = new ArrayList<>(); // List of trades associated with the stock

    /**
     * Constructs a Stock object with the given parameters.
     *
     * @param symbol          The stock symbol (e.g., TEA, POP).
     * @param type            The type of stock (COMMON or PREFERRED).
     * @param lastDividend    The last dividend in pennies.
     * @param fixedDividend   The fixed dividend for PREFERRED stock.
     * @param parValue        The par value of the stock.
     */
    public Stock(String symbol, String type, double lastDividend, double fixedDividend, double parValue) {
        this.symbol = symbol;
        this.type = type;
        this.lastDividend = lastDividend;
        this.fixedDividend = fixedDividend;
        this.parValue = parValue;
    }

    /**
     * Adds a trade to the list of trades associated with this stock.
     *
     * @param trade The trade to add.
     */
    public void addTrade(Trade trade) {
        this.trades.add(trade);
        logger.info("Added trade: " + trade + " to stock: " + symbol); // Log when a trade is added
    }

    /**
     * Returns an unmodifiable view of the list of trades to ensure immutability.
     *
     * @return An unmodifiable list of trades.
     */
    public List<Trade> getTrades() {
        return Collections.unmodifiableList(trades);
    }

    /**
     * Returns a string representation of the stock.
     *
     * @return A string representation of the stock object.
     */
    @Override
    public String toString() {
        return "Stock{" +
                "id=" + id +
                ", symbol='" + symbol + '\'' +
                ", type='" + type + '\'' +
                ", lastDividend=" + lastDividend +
                ", fixedDividend=" + fixedDividend +
                ", parValue=" + parValue +
                '}';
    }
}
