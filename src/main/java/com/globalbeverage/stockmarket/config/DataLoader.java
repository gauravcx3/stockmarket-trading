package com.globalbeverage.stockmarket.config;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataLoader implements CommandLineRunner {
    private final StockRepository stockRepository;

    public DataLoader(StockRepository stockRepository) {
        this.stockRepository = stockRepository;
    }

    @Override
    public void run(String... args) throws Exception {
        // Initialize sample stock data
        stockRepository.save(new Stock("TEA", "COMMON", 0.0, 0.0, 100.0));
        stockRepository.save(new Stock("POP", "COMMON", 8.0, 0.0, 100.0));
        stockRepository.save(new Stock("ALE", "COMMON", 23.0, 0.0, 60.0));
        stockRepository.save(new Stock("GIN", "PREFERRED", 8.0, 2.0, 100.0));
        stockRepository.save(new Stock("JOE", "COMMON", 13.0, 0.0, 250.0));
    }
}
