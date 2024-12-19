package com.globalbeverage.stockmarket.service;

import com.globalbeverage.stockmarket.domain.Stock;
import com.globalbeverage.stockmarket.repository.StockRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

public interface StockService {
    double calculateDividendYield(String symbol, double price);
    double calculatePERatio(String symbol, double price);
}

