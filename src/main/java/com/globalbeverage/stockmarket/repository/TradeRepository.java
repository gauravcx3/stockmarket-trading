package com.globalbeverage.stockmarket.repository;

import com.globalbeverage.stockmarket.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStockSymbol(String stockSymbol);
}
