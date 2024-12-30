package com.globalbeverage.stockmarket.mapper;

import com.globalbeverage.stockmarket.domain.Trade;
import jakarta.persistence.EntityManager;
import jakarta.persistence.TypedQuery;
import java.util.List;
import java.util.Optional;

/**
 * Mapper class for handling database operations related to {@link Trade} entities.
 */
public class TradeMapper {

    private final EntityManager entityManager;

    public TradeMapper(EntityManager entityManager) {
        this.entityManager = entityManager;
    }

    /**
     * Finds a trade by its ID.
     *
     * @param id The ID of the trade.
     * @return An Optional containing the trade if found, or an empty Optional if not found.
     */
    public Optional<Trade> findTradeById(Long id) {
        return Optional.ofNullable(entityManager.find(Trade.class, id));
    }

    /**
     * Finds all trades for a specific stock symbol.
     *
     * @param symbol The stock symbol.
     * @return A list of trades related to the given stock symbol.
     */
    public List<Trade> findTradesBySymbol(String symbol) {
        TypedQuery<Trade> query = entityManager.createQuery(
                "SELECT t FROM Trade t WHERE t.stock.symbol = :symbol", Trade.class);
        query.setParameter("symbol", symbol);
        return query.getResultList();
    }

    /**
     * Saves a trade entity to the database.
     *
     * @param trade The trade entity to be saved.
     * @return The saved trade entity.
     */
    public Trade saveTrade(Trade trade) {
        if (trade.getId() == null) {
            entityManager.persist(trade);
            return trade;
        } else {
            return entityManager.merge(trade);
        }
    }

    /**
     * Saves a list of trades to the database.
     *
     * @param trades The list of trades to be saved.
     * @return The list of saved trade entities.
     */
    public List<Trade> saveAllTrades(List<Trade> trades) {
        for (Trade trade : trades) {
            if (trade.getId() == null) {
                entityManager.persist(trade);
            } else {
                entityManager.merge(trade);
            }
        }
        return trades;
    }

    /**
     * Deletes a trade entity from the database.
     *
     * @param trade The trade entity to be deleted.
     */
    public void deleteTrade(Trade trade) {
        if (entityManager.contains(trade)) {
            entityManager.remove(trade);
        } else {
            entityManager.remove(entityManager.merge(trade));
        }
    }

    /**
     * Deletes a trade by its ID.
     *
     * @param id The ID of the trade to be deleted.
     */
    public void deleteTradeById(Long id) {
        Trade trade = entityManager.find(Trade.class, id);
        if (trade != null) {
            deleteTrade(trade);
        }
    }
}
