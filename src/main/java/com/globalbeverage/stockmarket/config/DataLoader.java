package com.globalbeverage.stockmarket.config;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.StockType;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * The DataLoader class provides functionality to load and process stock market data.
 * It supports both pre-set data and randomly generated data for testing and simulation purposes.
 */
@Component
public class DataLoader {

    private static final Logger logger = LoggerFactory.getLogger(DataLoader.class);
    private static final int BATCH_SIZE = 100; // Define batch size for processing data

    private final StockRepository stockRepository;

    public DataLoader(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    /**
     * Loads and processes stock data.
     *
     * @param usePresetData A boolean flag to determine whether to use pre-set data or randomly generated data.
     *                      If true, pre-set data is used; otherwise, random data is generated and used.
     */
    public void loadData(boolean usePresetData) {
        List<Stock> stockList = usePresetData ? getPresetData() : generateRandomData();

        logger.info("Loading data using " + (usePresetData ? "preset data" : "randomly generated data") +
                ". Total stocks to process: " + stockList.size());

        // Split the validation and saving logic
        List<Stock> validStocks = validateAndFilterStocks(stockList);
        saveStocksInBatches(validStocks);
    }

    /**
     * Validates and filters the stock list, separating valid stocks from invalid ones.
     *
     * @param stockList A list of Stock objects to validate.
     * @return A list of valid Stock objects.
     */
    private List<Stock> validateAndFilterStocks(List<Stock> stockList) {
        List<Stock> validStocks = new ArrayList<>();
        List<Stock> invalidStocks = new ArrayList<>();

        for (Stock stock : stockList) {
            try {
                validateData(stock);
                validStocks.add(stock);
            } catch (Exception e) {
                logger.error("Invalid stock data: " + stock, e); // Include stock data for debugging
                invalidStocks.add(stock); // Add invalid stock for logging purposes
            }
        }

        // Log any invalid stocks after processing
        if (!invalidStocks.isEmpty()) {
            logger.warn("There were invalid stocks that were not processed: " + invalidStocks);
        }
        return validStocks;
    }

    /**
     * Saves the valid stocks in batches to the repository.
     *
     * @param validStocks A list of valid Stock objects to be saved.
     */
    public void saveStocksInBatches(List<Stock> validStocks) {
        int count = 0;
        List<Stock> batch = new ArrayList<>();

        for (Stock stock : validStocks) {
            batch.add(stock);
            if (++count % BATCH_SIZE == 0) {
                logger.info("Saving batch of " + batch.size() + " stocks to the repository.");
                stockRepository.saveAll(batch);
                batch.clear();  // Clear batch after saving
            }
        }

        // Save any remaining stocks
        if (!batch.isEmpty()) {
            logger.info("Saving final batch of " + batch.size() + " stocks.");
            stockRepository.saveAll(batch);
        }
    }

    /**
     * Provides a predefined list of stock data based on the provided stocks.
     *
     * @return A list of Stock objects representing pre-set stock data.
     */
    private List<Stock> getPresetData() {
        return List.of(
                new Stock("TEA", StockType.COMMON, 0.0, 0.0, 100.0),
                new Stock("POP", StockType.COMMON, 8.0, 0.0, 100.0),
                new Stock("ALE", StockType.COMMON, 23.0, 0.0, 60.0),
                new Stock("GIN", StockType.PREFERRED, 8.0, 2.0, 100.0),
                new Stock("JOE", StockType.COMMON, 13.0, 0.0, 250.0)
        );
    }

    /**
     * Generates a random list of stock data for simulation purposes.
     *
     * @return A list of randomly generated Stock objects.
     */
    private List<Stock> generateRandomData() {
        List<Stock> stockList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            stockList.add(new Stock(
                    "TICKER" + i,
                    random.nextBoolean() ? StockType.COMMON : StockType.PREFERRED,
                    random.nextDouble() * 50,
                    random.nextDouble() * 5,
                    random.nextDouble() * 500
            ));
        }
        return stockList;
    }

    /**
     * Validates the given stock data.
     *
     * @param stock The Stock object to validate.
     * @throws IllegalArgumentException If the stock data contains invalid values (e.g., negative price, volume, or dividend).
     */
    private void validateData(Stock stock) throws IllegalArgumentException {
        String stockSymbol = stock.getSymbol();  // Correct method for getting the stock symbol

        if (stock.getLastDividend() < 0) {
            logger.error("Invalid LastDividend value for stock " + stockSymbol + ": " + stock.getLastDividend());
            throw new IllegalArgumentException("Invalid LastDividend value: " + stock.getLastDividend());
        }
        if (stock.getFixedDividend() < 0) {
            logger.error("Invalid FixedDividend value for stock " + stockSymbol + ": " + stock.getFixedDividend());
            throw new IllegalArgumentException("Invalid FixedDividend value: " + stock.getFixedDividend());
        }
        if (stock.getParValue() < 0) {
            logger.error("Invalid ParValue for stock " + stockSymbol + ": " + stock.getParValue());
            throw new IllegalArgumentException("Invalid ParValue: " + stock.getParValue());
        }
    }
}
