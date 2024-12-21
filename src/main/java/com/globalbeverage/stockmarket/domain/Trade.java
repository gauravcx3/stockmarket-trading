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

    private static final Logger logger = LoggerFactory.getLogger(Trade.class);

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotNull(message = "Stock symbol cannot be null")
    private String stockSymbol;

    @NotNull(message = "Timestamp cannot be null")
    @Column(name = "timestamp")
    private LocalDateTime timestamp;

    @Min(value = 1, message = "Quantity must be greater than 0")
    private int quantity;

    private boolean buy;

    @Min(value = 0, message = "Price must be non-negative")
    private double price;

    @ManyToOne
    @JoinColumn(name = "stock_id")
    private Stock stock;

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
        this.timestamp = (timestamp != null) ? timestamp : LocalDateTime.now();
        this.quantity = quantity;
        this.buy = buy;
        this.price = price;
        this.stock = stock;

        logger.info("New trade created: stockSymbol={}, quantity={}, price={}, stock={}", stockSymbol, quantity, price, stock.getSymbol());
    }

    /**
     * Automatically sets the timestamp to the current time before persisting the entity.
     */
    @PrePersist
    public void setDefaultTimestamp() {
        if (this.timestamp == null) {
            this.timestamp = LocalDateTime.now();
        }
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
