package com.globalbeverage.stockmarket.repository.impl;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.repository.StockRepository;
import com.globalbeverage.stockmarket.mapper.StockMapper;
import jakarta.persistence.EntityManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;

/**
 * Custom implementation of the StockRepository interface.
 * This class delegates persistence operations to the StockMapper class
 * and logs key actions and results.
 */
@Component
public class StockRepositoryImpl implements StockRepository {

    private static final Logger logger = LoggerFactory.getLogger(StockRepositoryImpl.class);
    private final StockMapper stockMapper;

    /**
     * Constructor to initialize the repository with the given EntityManager.
     * The EntityManager is used to create a StockMapper instance for database operations.
     *
     * @param entityManager The EntityManager used to interact with the database.
     */
    public StockRepositoryImpl(EntityManager entityManager) {
        this.stockMapper = new StockMapper(entityManager);
    }

    /**
     * Finds a stock by its symbol.
     *
     * @param symbol The symbol of the stock (e.g., TEA, POP).
     * @return An Optional containing the found stock, or an empty Optional if no stock is found with the given symbol.
     */
    @Override
    public Optional<Stock> findStockBySymbol(String symbol) {
        logger.info("Looking for stock with symbol: {}", symbol);
        Optional<Stock> stock = stockMapper.findStockBySymbol(symbol);
        if (stock.isEmpty()) {
            logger.warn("No stock found with symbol: {}", symbol);
        }
        return stock;
    }

    /**
     * Finds all stocks of a specific type.
     *
     * @param type The type of the stock (e.g., "COMMON", "PREFERRED").
     * @return A list of stocks that match the specified type.
     */
    @Override
    public List<Stock> findByType(String type) {
        logger.info("Finding stocks of type: {}", type);
        List<Stock> stocks = stockMapper.findByType(type);
        logger.info("Found {} stocks of type: {}", stocks.size(), type);
        return stocks;
    }

    /**
     * Finds all stocks with a par value greater than the specified minimum.
     *
     * @param minParValue The minimum par value to filter stocks by.
     * @return A list of stocks with a par value greater than the given value.
     */
    @Override
    public List<Stock> findByParValueGreaterThan(double minParValue) {
        logger.info("Finding stocks with par value greater than: {}", minParValue);
        List<Stock> stocks = stockMapper.findByParValueGreaterThan(minParValue);
        logger.info("Found {} stocks with par value greater than: {}", stocks.size(), minParValue);
        return stocks;
    }

    /**
     * Finds all stocks in the database.
     *
     * @return A list of all stocks stored in the database.
     */
    @Override
    public List<Stock> findAllStocks() {
        logger.info("Finding all stocks in the database");
        List<Stock> stocks = stockMapper.findAllStocks();
        logger.info("Found {} stocks in the database", stocks.size());
        return stocks;
    }

    /**
     * Saves a stock to the database.
     * If the stock has an ID, it is updated; otherwise, a new stock is created.
     *
     * @param stock The stock entity to be saved or updated.
     * @return The saved or updated stock entity.
     */
    @Override
    public Stock saveStock(Stock stock) {
        logger.info("Saving stock: {}", stock);
        if (stock.getId() == null) {
            return stockMapper.saveStock(stock);
        } else {
            return stockMapper.updateStock(stock);
        }
    }

    /**
     * Saves a list of stocks to the database.
     * If a stock has an ID, it is updated; otherwise, a new stock is created.
     *
     * @param stocks The list of stocks to be saved or updated.
     * @return The list of saved or updated stocks.
     */
    @Override
    public List<Stock> saveAllStocks(List<Stock> stocks) {
        logger.info("Saving multiple stocks. Count: {}", stocks.size());
        return stockMapper.saveAllStocks(stocks);
    }

    /**
     * Deletes a stock from the database.
     *
     * @param stock The stock entity to be deleted.
     */
    @Override
    public void deleteStock(Stock stock) {
        logger.info("Attempting to delete stock: {}", stock);
        stockMapper.deleteStock(stock);
    }

    /**
     * Deletes a stock from the database by its ID.
     *
     * @param id The ID of the stock to be deleted.
     */
    @Override
    public void deleteStockById(Long id) {
        logger.info("Deleting stock by id: {}", id);
        stockMapper.deleteStockById(id);
    }
}
