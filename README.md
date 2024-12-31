# Stock Market Trading

## Project Overview

This project is part of a StockMarket App Assignment for 2024. The goal of the project is to implement part of the core object model for a stock market trading system in a drinks company environment. The system should calculate key stock metrics, record trades, and calculate the GBCE All Share Index using provided formulas.

## Requirements

### Stock Operations:
- **Calculate Dividend Yield** for a given stock price.
- **Calculate P/E Ratio** for a given stock price.
- **Record trades** with a timestamp, quantity, buy/sell indicator, and price.
- **Calculate Volume Weighted Stock Price** based on trades in the past 5 minutes.

### Global Beverage Corporation Exchange (GBCE):
- **Calculate the GBCE All Share Index** using the geometric mean of the Volume Weighted Stock Price for all stocks.

## Software Requirements

- **JDK (Java Development Kit)**: Version 11 or higher
- **Maven** (for managing dependencies and building the project)
- **JUnit 5** (for running unit tests)

## Project Setup and Installation

1. **Clone the repository**:
    ```bash
    git clone https://github.com/gauravcx3/stockmarket-trading.git
    cd stockmarket-trading
    ```

2. **Build the project**:
    You can use Maven to compile the code:
    ```bash
    mvn clean install
    ```

3. **Run the tests**:
    To execute the unit tests, run:
    ```bash
    mvn test
    ```

4. **Running the Application**:
    The project is designed as a collection of classes that model the stock market system. Since it doesn’t have a GUI or persistent storage, you can interact with the model via tests or by extending the implementation.

## Code Structure

- **`src/main/java`**: Contains the core classes for the stock market system.
  - **`com.globalbeverage.stockmarket.config`**: Configuration classes and test data setup.
  - **`com.globalbeverage.stockmarket.domain`**: Domain classes representing the core objects like stock and trade.
  - **`com.globalbeverage.stockmarket.exception`**: Custom exceptions for handling errors.
  - **`com.globalbeverage.stockmarket.mapper`**: Classes for mapping domain objects to other formats.
  - **`com.globalbeverage.stockmarket.repository`**: Interfaces for data access (in-memory).
  - **`com.globalbeverage.stockmarket.service`**: Business logic for stock market operations.

- **`src/test/java`**: Contains unit tests.
  - **`com.globalbeverage.stockmarket.config`**: Tests for configuration classes.
  - **`com.globalbeverage.stockmarket.stock`**: Tests for stock-related functionality.
  - **`com.globalbeverage.stockmarket.trade`**: Tests for trade-related functionality.

## Features

- **Dividend Yield Calculation**: Calculate the dividend yield based on the stock type and price.
- **P/E Ratio Calculation**: Calculate the Price to Earnings ratio for a given stock price.
- **Trade Recording**: Record buy and sell trades with quantity, price, and timestamp.
- **Volume Weighted Stock Price**: Calculate the Volume Weighted Stock Price based on recent trades.
- **GBCE All Share Index**: Calculate the GBCE All Share Index using the geometric mean of the Volume Weighted Stock Price for all stocks.

## Testing

The project uses JUnit for unit testing. The tests validate different components, including trade recording, stock metrics calculations, and error handling.

To run the tests, simply use Maven:

```bash
mvn test
