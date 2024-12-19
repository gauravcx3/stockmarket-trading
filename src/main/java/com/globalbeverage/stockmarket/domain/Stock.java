package com.globalbeverage.stockmarket.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@NoArgsConstructor
@AllArgsConstructor
@Data
public class Stock {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String symbol;          // Stock symbol (e.g., TEA, POP)
    private String type;            // COMMON or PREFERRED
    private double lastDividend;    // Last dividend in pennies
    private double fixedDividend;   // Fixed dividend (for PREFERRED)
    private double parValue;        // Par value
    private List<Trade> trades = new ArrayList<>();

    public Stock(String symbol, String type, double lastDividend, double fixedDividend, double parValue) {
        this.symbol = symbol;
        this.type = type;
        this.lastDividend = lastDividend;
        this.fixedDividend = fixedDividend;
        this.parValue = parValue;
    }

    public void addTrade(Trade trade) {
        this.trades.add(trade);
    }
}
