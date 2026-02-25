package persistance.fileimplementation;

import entities.StockPriceHistory;
import persistance.interfaces.StockPriceHistoryDAO;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class FileStockPriceHistoryDAO implements StockPriceHistoryDAO
{
  FileUnitOfWork uow;

  public FileStockPriceHistoryDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void createStockPriceHistory(StockPriceHistory stockPriceHistory)
  {
    uow.getStockPriceHistoryList().add(stockPriceHistory);
  }

  @Override public void appendStockPriceHistory(StockPriceHistory stockPriceHistory)
  {
    uow.getStockPriceHistoryAppendOnly().add(stockPriceHistory);
  }

  @Override public StockPriceHistory getStockPriceHistoryById(String id)
  {
    List<StockPriceHistory> history = uow.getStockPriceHistoryList();
    for (StockPriceHistory stockPriceHistory : history)
    {
      if (stockPriceHistory.getId().equals(id))
      {
        return stockPriceHistory;
      }
    }
    Logger.getInstance().log("ERROR", "StockPriceHistory not found: " + id);
    throw new RuntimeException("StockPriceHistory not found: " + id);
  }

  @Override public List<StockPriceHistory> getStockPriceHistoryBySymbol(String stockSymbol)
  {
    List<StockPriceHistory> history = uow.getStockPriceHistoryList();
    List<StockPriceHistory> result = new ArrayList<>();
    for (StockPriceHistory stockPriceHistory : history)
    {
      if (stockPriceHistory.getStockSymbol().equals(stockSymbol))
      {
        result.add(StockPriceHistory.reloadFromStorage(stockPriceHistory.getId(),
            stockPriceHistory.getStockSymbol(), stockPriceHistory.getPrice(),
            stockPriceHistory.getTimestamp()));
      }
    }
    return result;
  }

  @Override public List<StockPriceHistory> getAllStockPriceHistory()
  {
    List<StockPriceHistory> history = uow.getStockPriceHistoryList();
    List<StockPriceHistory> historyCopy = new ArrayList<>();
    for (StockPriceHistory stockPriceHistory : history)
    {
      historyCopy.add(StockPriceHistory.reloadFromStorage(stockPriceHistory.getId(),
          stockPriceHistory.getStockSymbol(), stockPriceHistory.getPrice(),
          stockPriceHistory.getTimestamp()));
    }
    return historyCopy;
  }
}