package com.globalbeverage.stockmarket.domain;

import jakarta.persistence.*;
import lombok.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;

import java.time.LocalDateTime;

/**
 * Represents a trade of a stock, including details such as stock symbol,
 * timestamp, quantity, buy/sell indicator, and price.
 */
@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {

    private static final Logger logger = LoggerFactory.getLogger(Trade.class); // Logger for Trade class

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Stock symbol cannot be null") // Ensures the stock symbol is provided
    private String stockSymbol;     // The symbol of the stock involved in the trade.

    @NotNull(message = "Timestamp cannot be null") // Ensures the timestamp is provided
    @Column(name = "timestamp") // Maps to a column in the database named "timestamp"
    private LocalDateTime timestamp; // The date and time the trade was executed.

    @Min(value = 1, message = "Quantity must be greater than 0") // Ensures quantity is a positive integer
    private int quantity;            // The number of shares traded.

    private boolean buy;             // Indicates whether the trade was a buy (true) or a sell (false).

    @Min(value = 0, message = "Price must be non-negative") // Ensures price is non-negative
    private double price;            // The price per share at which the trade was executed.

    @ManyToOne // Many trades can be associated with one stock
    @JoinColumn(name = "stock_id")  // Foreign key column for Stock in the Trade table
    private Stock stock;  // Reference to the associated Stock

    /**
     * Constructs a new Trade object.
     *
     * @param stockSymbol The symbol of the stock involved in the trade.
     * @param timestamp The timestamp when the trade occurred.
     * @param quantity The quantity of stock traded.
     * @param buy Indicates whether the trade was a buy (true) or a sell (false).
     * @param price The price per share at which the trade was executed.
     * @param stock The stock associated with the trade.
     */
    public Trade(String stockSymbol, LocalDateTime timestamp, int quantity, boolean buy, double price, Stock stock) {
        this.stockSymbol = stockSymbol;
        this.timestamp = timestamp;
        this.quantity = quantity;
        this.buy = buy;
        this.price = price;
        this.stock = stock;  // Set the stock object for this trade

        // Log trade creation
        logger.info("New trade created: stockSymbol={}, quantity={}, price={}, stock={}", stockSymbol, quantity, price, stock.getSymbol());
    }

    /**
     * Returns a string representation of the trade.
     *
     * @return A string representation of the trade object.
     */
    @Override
    public String toString() {
        return "Trade{" +
                "id=" + id +
                ", stockSymbol='" + stockSymbol + '\'' +
                ", timestamp=" + timestamp +
                ", quantity=" + quantity +
                ", buy=" + buy +
                ", price=" + price +
                ", stock=" + stock.getSymbol() +
                '}';
    }
}
