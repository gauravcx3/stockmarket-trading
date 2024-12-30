package com.globalbeverage.stockmarket.mapper;

import com.globalbeverage.stockmarket.domain.Stock;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Mapper class that handles the persistence operations for {@link Stock} entities.
 * Provides methods for querying, saving, updating, and deleting stocks.
 */
public class StockMapper {

    private final EntityManager entityManager;

    /**
     * Constructor to initialize the StockMapper with the given EntityManager.
     *
     * @param entityManager The EntityManager instance used for interacting with the database.
     */
    public StockMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Finds a stock by its symbol.
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @return An Optional containing the found stock, or an empty Optional if no stock is found with the given symbol.
     */
    public Optional<Stock> findStockBySymbol(String symbol) {
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s WHERE s.symbol = :symbol", Stock.class);
        query.setParameter("symbol", symbol);
        List<Stock> results = query.getResultList();
        return results.isEmpty() ? Optional.empty() : Optional.of(results.get(0));
    }

    /**
     * Finds all stocks of a specific type.
     *
     * @param type The type of the stock (e.g., "COMMON", "PREFERRED").
     * @return A list of stocks that match the specified type.
     */
    public List<Stock> findByType(String type) {
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s WHERE s.type = :type", Stock.class);
        query.setParameter("type", type);
        return query.getResultList();
    }

    /**
     * Finds all stocks with a par value greater than the specified minimum.
     *
     * @param minParValue The minimum par value to filter stocks by.
     * @return A list of stocks with a par value greater than the given value.
     */
    public List<Stock> findByParValueGreaterThan(double minParValue) {
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s WHERE s.parValue > :minParValue", Stock.class);
        query.setParameter("minParValue", minParValue);
        return query.getResultList();
    }

    /**
     * Finds all stocks in the database.
     *
     * @return A list of all stocks stored in the database.
     */
    public List<Stock> findAllStocks() {
        TypedQuery<Stock> query = entityManager.createQuery("SELECT s FROM Stock s", Stock.class);
        return query.getResultList();
    }

    /**
     * Saves a new stock to the database.
     *
     * @param stock The stock entity to be saved.
     * @return The saved stock entity.
     */
    public Stock saveStock(Stock stock) {
        entityManager.persist(stock);
        return stock;
    }

    /**
     * Saves a list of stocks to the database. If a stock has an ID, it is updated; otherwise, a new stock is created.
     *
     * @param stocks The list of stocks to be saved or updated.
     * @return The list of saved or updated stocks.
     */
    public List<Stock> saveAllStocks(List<Stock> stocks) {
        for (Stock stock : stocks) {
            if (stock.getId() == null) {
                entityManager.persist(stock);
            } else {
                entityManager.merge(stock);
            }
        }
        return stocks;
    }

    /**
     * Updates an existing stock in the database.
     *
     * @param stock The stock entity to be updated.
     * @return The updated stock entity.
     */
    public Stock updateStock(Stock stock) {
        return entityManager.merge(stock);
    }

    /**
     * Deletes a stock from the database.
     *
     * @param stock The stock entity to be deleted.
     */
    public void deleteStock(Stock stock) {
        if (entityManager.contains(stock)) {
            entityManager.remove(stock);
        } else {
            entityManager.remove(entityManager.merge(stock));
        }
    }

    /**
     * Deletes a stock from the database by its ID.
     *
     * @param id The ID of the stock to be deleted.
     */
    public void deleteStockById(Long id) {
        Stock stock = entityManager.find(Stock.class, id);
        if (stock != null) {
            deleteStock(stock);
        }
    }
}
