package com.globalbeverage.stockmarket.repository;

import com.globalbeverage.stockmarket.domain.Stock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repository for accessing and performing CRUD operations on {@link Stock} entities.
 * Custom methods for managing stocks in the database are declared here.
 */
@Repository
public interface StockRepository {

    /**
     * Finds a stock by its symbol.
     *
     * @param symbol The stock symbol (e.g., TEA, POP).
     * @return An Optional containing the stock if found, or an empty Optional if not found.
     */
    Optional<Stock> findStockBySymbol(String symbol);

    /**
     * Finds all stocks of a given type (e.g., "COMMON" or "PREFERRED").
     *
     * @param type The type of stock (e.g., "COMMON", "PREFERRED").
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

    /**
     * Finds all stocks in the database.
     *
     * @return A list of all stocks in the database.
     */
    List<Stock> findAllStocks();

    /**
     * Custom method to save or update a stock entity.
     *
     * @param stock The stock entity to save or update.
     * @return The saved or updated stock entity.
     */
    Stock saveStock(Stock stock);

    /**
     * Custom method to save or update a list of stock entities.
     *
     * @param stocks The list of stock entities to save or update.
     * @return The saved or updated stock entities.
     */
    List<Stock> saveAllStocks(List<Stock> stocks);

    /**
     * Custom method to delete a stock by its ID.
     *
     * @param id The ID of the stock to be deleted.
     */
    void deleteStockById(Long id);

    /**
     * Custom method to delete a stock entity.
     *
     * @param stock The stock entity to delete.
     */
    void deleteStock(Stock stock);
}
