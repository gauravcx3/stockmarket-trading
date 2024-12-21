package com.globalbeverage.stockmarket.repository;

import com.globalbeverage.stockmarket.domain.Trade;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

/**
 * Repository interface for accessing and managing {@link Trade} entities.
 * Extends JpaRepository to provide basic CRUD operations and custom query methods.
 */
public interface TradeRepository extends JpaRepository<Trade, Long> {

    /**
     * Retrieves a list of trades associated with a specific stock symbol.
     *
     * @param stockSymbol The symbol of the stock for which trades are to be retrieved.
     * @return A list of trades for the specified stock symbol.
     */
    List<Trade> findByStockSymbol(String stockSymbol);
}
