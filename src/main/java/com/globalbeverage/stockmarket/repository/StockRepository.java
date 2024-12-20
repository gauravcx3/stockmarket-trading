package com.globalbeverage.stockmarket.repository;

import com.globalbeverage.stockmarket.domain.Stock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository interface for performing CRUD operations on {@link Stock} entities.
 * This interface extends JpaRepository, providing built-in methods for database operations.
 */
@Repository
public interface StockRepository extends JpaRepository<Stock, Long> {

    /**
     * Finds a stock by its symbol.
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @return An Optional containing the stock if found, or an empty Optional if not found.
     */
    Optional<Stock> findBySymbol(String symbol);

    /**
     * Finds all stocks of a given type.
     *
     * @param type The type of the stock (e.g., "COMMON", "PREFERRED").
     * @return A list of stocks of the given type.
     */
    List<Stock> findByType(String type);

    /**
     * Finds all stocks with a par value greater than a given amount.
     *
     * @param minParValue The minimum par value to filter stocks by.
     * @return A list of stocks with a par value greater than the specified amount.
     */
    List<Stock> findByParValueGreaterThan(double minParValue);
}
