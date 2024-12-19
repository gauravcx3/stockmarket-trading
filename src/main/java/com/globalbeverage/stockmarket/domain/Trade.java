package com.globalbeverage.stockmarket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import java.time.LocalDateTime;

import lombok.*;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Trade {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String stockSymbol;     // Stock symbol
    private LocalDateTime timestamp;
    private int quantity;
    private boolean isBuy;          // true = Buy, false = Sell
    private double price;

    public Trade(String stockSymbol, LocalDateTime timestamp, int quantity, boolean isBuy, double price) {
        this.stockSymbol = stockSymbol;
        this.timestamp = timestamp;
        this.quantity = quantity;
        this.isBuy = isBuy;
        this.price = price;
    }
}
