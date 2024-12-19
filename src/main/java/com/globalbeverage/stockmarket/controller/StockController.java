package com.globalbeverage.stockmarket.controller;

import com.globalbeverage.stockmarket.service.StockService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/stocks")
public class StockController {
    @Autowired
    private StockService stockService;

    @GetMapping("/{symbol}/dividendYield")
    public double getDividendYield(@PathVariable String symbol, @RequestParam double price) {
        return stockService.calculateDividendYield(symbol, price);
    }
}
