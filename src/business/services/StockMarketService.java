package business.services;

import entities.Stock;
import entities.StockPriceHistory;
import persistance.interfaces.StockDAO;
import persistance.interfaces.StockPriceHistoryDAO;

import java.util.List;

public class StockMarketService
{
  private final StockDAO stockDAO;
  private final StockPriceHistoryDAO stockPriceHistoryDAO;

  public StockMarketService(StockDAO stockDAO,
      StockPriceHistoryDAO stockPriceHistoryDAO)
  {
    this.stockDAO = stockDAO;
    this.stockPriceHistoryDAO = stockPriceHistoryDAO;
  }

  public List<Stock> getAllStocks()
  {
    return stockDAO.getAllStocks();
  }

  public List<StockPriceHistory> getStockPriceHistory(String symbol)
  {
    return stockPriceHistoryDAO.getStockPriceHistoryBySymbol(symbol);
  }
}
