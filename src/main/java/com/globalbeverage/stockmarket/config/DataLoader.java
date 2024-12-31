package com.globalbeverage.stockmarket.config;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.domain.StockType;
import com.globalbeverage.stockmarket.domain.Trade;
import com.globalbeverage.stockmarket.repository.StockRepository;
import com.globalbeverage.stockmarket.repository.TradeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
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
    private static final int BATCH_SIZE = 100;

    private final StockRepository stockRepository;
    private final TradeRepository tradeRepository;

    public DataLoader(StockRepository stockRepository, TradeRepository tradeRepository) {
        this.stockRepository = stockRepository;
        this.tradeRepository = tradeRepository;
    }

    /**
     * Loads and processes stock and trade data.
     *
     * @param usePresetData A boolean flag to determine whether to use pre-set data or randomly generated data.
     */
    public void loadData(boolean usePresetData) {
        List<Stock> stockList = usePresetData ? getPresetData() : generateRandomData();
        List<Trade> tradeList = generateRandomTrades(stockList);

        logger.info("Loading data using " + (usePresetData ? "preset data" : "randomly generated data") +
                ". Total stocks to process: " + stockList.size() + ", total trades to process: " + tradeList.size());

        List<Stock> validStocks = validateAndFilterStocks(stockList);
        saveStocksInBatches(validStocks);

        List<Trade> validTrades = validateAndFilterTrades(tradeList);
        saveTradesInBatches(validTrades);
    }

    /**
     * Validates and filters the trade list, separating valid trades from invalid ones.
     *
     * @param tradeList A list of Trade objects to validate.
     * @return A list of valid Trade objects.
     */
    private List<Trade> validateAndFilterTrades(List<Trade> tradeList) {
        List<Trade> validTrades = new ArrayList<>();
        List<Trade> invalidTrades = new ArrayList<>();

        for (Trade trade : tradeList) {
            try {
                validateTradeData(trade);
                validTrades.add(trade);
            } catch (Exception e) {
                logger.error("Invalid trade data: " + trade, e);
                invalidTrades.add(trade);
            }
        }

        if (!invalidTrades.isEmpty()) {
            logger.warn("There were invalid trades that were not processed: " + invalidTrades);
        }
        return validTrades;
    }

    /**
     * Saves the valid trades in batches to the repository.
     *
     * @param validTrades A list of valid Trade objects to be saved.
     */
    private void saveTradesInBatches(List<Trade> validTrades) {
        int count = 0;
        List<Trade> batch = new ArrayList<>();

        for (Trade trade : validTrades) {
            batch.add(trade);
            if (++count % BATCH_SIZE == 0) {
                logger.info("Saving batch of " + batch.size() + " trades to the repository.");
                tradeRepository.saveAllTrades(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            logger.info("Saving final batch of " + batch.size() + " trades.");
            tradeRepository.saveAllTrades(batch);
        }
    }

    /**
     * Generates a random list of trades based on the provided list of stocks.
     *
     * @param stocks A list of Stock objects to generate trades for.
     * @return A list of randomly generated Trade objects.
     */
    private List<Trade> generateRandomTrades(List<Stock> stocks) {
        List<Trade> tradeList = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 1000; i++) {
            Stock stock = stocks.get(random.nextInt(stocks.size()));
            boolean buy = random.nextBoolean();
            int quantity = random.nextInt(100) + 1;
            double price = random.nextDouble() * 100;

            tradeList.add(new Trade(
                    stock.getSymbol(),
                    LocalDateTime.now(),
                    quantity,
                    buy,
                    price,
                    stock
            ));
        }

        return tradeList;
    }

    /**
     * Validates the given trade data.
     *
     * @param trade The Trade object to validate.
     * @throws IllegalArgumentException If the trade data contains invalid values.
     */
    private void validateTradeData(Trade trade) throws IllegalArgumentException {
        if (trade.getQuantity() <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than 0.");
        }
        if (trade.getPrice() < 0) {
            throw new IllegalArgumentException("Price must be non-negative.");
        }
        if (trade.getStock() == null) {
            throw new IllegalArgumentException("Stock cannot be null.");
        }
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
                logger.error("Invalid stock data: " + stock, e);
                invalidStocks.add(stock);
            }
        }

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
    private void saveStocksInBatches(List<Stock> validStocks) {
        int count = 0;
        List<Stock> batch = new ArrayList<>();

        for (Stock stock : validStocks) {
            batch.add(stock);
            if (++count % BATCH_SIZE == 0) {
                logger.info("Saving batch of " + batch.size() + " stocks to the repository.");
                stockRepository.saveAllStocks(batch);
                batch.clear();
            }
        }

        if (!batch.isEmpty()) {
            logger.info("Saving final batch of " + batch.size() + " stocks.");
            stockRepository.saveAllStocks(batch);
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
        String stockSymbol = stock.getSymbol();

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
