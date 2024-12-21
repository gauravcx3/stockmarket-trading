package com.globalbeverage.stockmarket.domain;

import jakarta.persistence.*;
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

    private static final Logger logger = LoggerFactory.getLogger(Stock.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Symbol cannot be null")
    private String symbol;

    @NotNull(message = "Stock type cannot be null")
    @Enumerated(EnumType.STRING)
    private StockType type;

    @Min(value = 0, message = "Last dividend must be non-negative")
    private double lastDividend;

    private double fixedDividend;

    @Min(value = 0, message = "Par value must be non-negative")
    private double parValue;

    @OneToMany(mappedBy = "stock", cascade = CascadeType.ALL, orphanRemoval = true)
    private final List<Trade> trades = new ArrayList<>();

    /**
     * Constructs a Stock object with the given parameters.
     *
     * @param symbol        The stock symbol (e.g., TEA, POP).
     * @param type          The type of stock (COMMON or PREFERRED).
     * @param lastDividend  The last dividend in pennies.
     * @param fixedDividend The fixed dividend for PREFERRED stock.
     * @param parValue      The par value of the stock.
     */
    public Stock(String symbol, StockType type, double lastDividend, double fixedDividend, double parValue) {
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
        logger.info("Added trade: " + trade + " to stock: " + symbol);
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
                ", type=" + type +
                ", lastDividend=" + lastDividend +
                ", fixedDividend=" + fixedDividend +
                ", parValue=" + parValue +
                '}';
    }
}
