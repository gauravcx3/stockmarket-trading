package com.globalbeverage.stockmarket.repository;

import com.globalbeverage.stockmarket.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface TradeRepository extends JpaRepository<Trade, Long> {
    List<Trade> findByStockSymbolAndTimestampAfter(String stockSymbol, LocalDateTime timestamp);
}
