package com.globalbeverage.stockmarket.repository.impl;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.repository.StockRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Custom implementation of the StockRepository for accessing and performing CRUD operations
 * on {@link Stock} entities. This class directly interacts with the EntityManager to perform database operations.
 */
@Component
public class StockRepositoryImpl implements StockRepository {

    private static final Logger logger = LoggerFactory.getLogger(StockRepositoryImpl.class);
    private final EntityManager entityManager;

    /**
     * Constructor to initialize the repository with the provided EntityManager.
     *
     * @param entityManager The EntityManager instance for interacting with the database.
     */
    public StockRepositoryImpl(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Finds a stock by its symbol.
     *
     * @param symbol The stock symbol (e.g., TEA, POP).
     * @return An Optional containing the stock if found, or an empty Optional if not found.
     */
    @Override
    public Optional<Stock> findStockBySymbol(String symbol) {
        logger.info("Looking for stock with symbol: {}", symbol);
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s WHERE s.symbol = :symbol", Stock.class);
        query.setParameter("symbol", symbol);
        List<Stock> results = query.getResultList();
        if (results.isEmpty()) {
            logger.warn("No stock found with symbol: {}", symbol);
            return Optional.empty();
        }
        logger.info("Stock found: {}", results.get(0));
        return Optional.of(results.get(0));
    }

    /**
     * Finds all stocks of a given type (e.g., "COMMON" or "PREFERRED").
     *
     * @param type The type of stock (e.g., "COMMON", "PREFERRED").
     * @return A list of stocks of the specified type.
     */
    @Override
    public List<Stock> findByType(String type) {
        logger.info("Finding stocks of type: {}", type);
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s WHERE s.type = :type", Stock.class);
        query.setParameter("type", type);
        List<Stock> results = query.getResultList();
        logger.info("Found {} stocks of type: {}", results.size(), type);
        return results;
    }

    /**
     * Finds all stocks with a par value greater than a specified amount.
     *
     * @param minParValue The minimum par value to filter stocks by.
     * @return A list of stocks with a par value greater than the given amount.
     */
    @Override
    public List<Stock> findByParValueGreaterThan(double minParValue) {
        logger.info("Finding stocks with par value greater than: {}", minParValue);
        TypedQuery<Stock> query = entityManager.createQuery(
                "SELECT s FROM Stock s WHERE s.parValue > :minParValue", Stock.class);
        query.setParameter("minParValue", minParValue);
        List<Stock> results = query.getResultList();
        logger.info("Found {} stocks with par value greater than: {}", results.size(), minParValue);
        return results;
    }

    /**
     * Saves a stock to the database. If the stock has an ID, it will be updated; otherwise, it will be inserted.
     *
     * @param stock The stock entity to be saved or updated.
     * @return The saved stock entity.
     */
    @Override
    public Stock saveStock(Stock stock) {
        if (stock.getId() == null) {
            logger.info("Persisting new stock: {}", stock);
            entityManager.persist(stock);
        } else {
            logger.info("Merging stock with id: {}", stock.getId());
            entityManager.merge(stock);
        }
        return stock;
    }

    /**
     * Saves multiple stocks to the database.
     *
     * @param stocks The list of stocks to be saved or updated.
     * @return The list of saved stocks.
     */
    @Override
    public List<Stock> saveAllStocks(List<Stock> stocks) {
        logger.info("Saving multiple stocks. Count: {}", stocks.size());
        for (Stock stock : stocks) {
            if (stock.getId() == null) {
                logger.info("Persisting new stock: {}", stock);
                entityManager.persist(stock);
            } else {
                logger.info("Merging stock with id: {}", stock.getId());
                entityManager.merge(stock);
            }
        }
        return stocks;
    }

    /**
     * Deletes the given stock from the database.
     *
     * @param stock The stock entity to be deleted.
     */
    @Override
    public void deleteStock(Stock stock) {
        logger.info("Attempting to delete stock: {}", stock);
        if (entityManager.contains(stock)) {
            entityManager.remove(stock);
            logger.info("Stock removed: {}", stock);
        } else {
            entityManager.remove(entityManager.merge(stock));
            logger.info("Stock merged and removed: {}", stock);
        }
    }

    /**
     * Deletes a stock from the database by its ID.
     *
     * @param id The ID of the stock to be deleted.
     */
    @Override
    public void deleteStockById(Long id) {
        Stock stock = entityManager.find(Stock.class, id);
        if (stock != null) {
            deleteStock(stock);
            logger.info("Stock with ID: {} deleted", id);
        } else {
            logger.warn("Stock with ID: {} not found for deletion", id);
        }
    }

    /**
     * Finds all stocks in the database.
     *
     * @return A list of all stocks in the database.
     */
    @Override
    public List<Stock> findAllStocks() {
        logger.info("Finding all stocks in the database");
        TypedQuery<Stock> query = entityManager.createQuery("SELECT s FROM Stock s", Stock.class);
        List<Stock> results = query.getResultList();
        logger.info("Found {} stocks in the database", results.size());
        return results;
    }
}
