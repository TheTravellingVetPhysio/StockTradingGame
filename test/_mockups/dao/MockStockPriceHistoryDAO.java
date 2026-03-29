package _mockups.dao;

import entities.StockPriceHistory;
import persistance.interfaces.StockPriceHistoryDAO;
import shared.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class MockStockPriceHistoryDAO implements StockPriceHistoryDAO
{
  private List<StockPriceHistory> history = new ArrayList<>();

  @Override public void createStockPriceHistory(
      StockPriceHistory stockPriceHistory)
  {
    history.add(stockPriceHistory);
  }

  @Override public void appendStockPriceHistory(
      StockPriceHistory stockPriceHistory)
  {
    history.add(stockPriceHistory);
  }

  @Override public StockPriceHistory getStockPriceHistoryById(String id)
  {
    for (StockPriceHistory stockPriceHistory : history)
    {
      if (stockPriceHistory.getId().equals(id))
      {
        return stockPriceHistory;
      }
    }
    throw new NotFoundException("StockPriceHistory not found: " + id);
  }

  @Override public List<StockPriceHistory> getStockPriceHistoryBySymbol(
      String stockSymbol)
  {
    List<StockPriceHistory> result = new ArrayList<>();
    for (StockPriceHistory stockPriceHistory : history)
    {
      if (stockPriceHistory.getStockSymbol().equals(stockSymbol))
      {
        result.add(
            StockPriceHistory.reloadFromStorage(stockPriceHistory.getId(),
                stockPriceHistory.getStockSymbol(),
                stockPriceHistory.getPrice(),
                stockPriceHistory.getTimestamp()));
      }
    }
    return result;
  }

  @Override public List<StockPriceHistory> getAllStockPriceHistory()
  {
    List<StockPriceHistory> historyCopy = new ArrayList<>();
    for (StockPriceHistory stockPriceHistory : history)
    {
      historyCopy.add(
          StockPriceHistory.reloadFromStorage(stockPriceHistory.getId(),
              stockPriceHistory.getStockSymbol(), stockPriceHistory.getPrice(),
              stockPriceHistory.getTimestamp()));
    }
    return historyCopy;
  }

}
