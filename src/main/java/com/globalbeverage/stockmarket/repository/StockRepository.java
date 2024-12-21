package com.globalbeverage.stockmarket.repository;

import com.globalbeverage.stockmarket.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

/**
 * Repository interface for accessing and performing CRUD operations on {@link Stock} entities.
 * Extends JpaRepository to leverage built-in methods for managing stocks in the database.
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * Finds a stock by its symbol.
     *
     * @param symbol The stock symbol (e.g., TEA, POP).
     * @return An Optional containing the stock if found, or an empty Optional if not found.
     */
    Optional<Stock> findBySymbol(String symbol);

    /**
     * Finds all stocks of a given type (e.g., "COMMON" or "PREFERRED").
     *
     * @param type The type of stock.
     * @return A list of stocks of the specified type.
     */
    List<Stock> findByType(String type);

    /**
     * Finds all stocks with a par value greater than a specified amount.
     *
     * @param minParValue The minimum par value to filter stocks by.
     * @return A list of stocks with a par value greater than the given amount.
     */
    List<Stock> findByParValueGreaterThan(double minParValue);
}
