package business.services;

import dto.StockDTO;
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

  public StockDTO getStock(String symbol)
  {
    Stock stock = stockDAO.getStockBySymbol(symbol);
    return new StockDTO(stock.getSymbol(), stock.getName(), stock.getCurrentPrice(), stock.getCurrentState());
  }

  public List<Stock> getAllStocks()   // Burde være en DTO for ikke at eksponere stock entiteter direkte. De bliver brugt i StockMarketViewModel hvor de med det samme bliver mappet til en StockUI entitet.
  {
    return stockDAO.getAllStocks();
  }

  public List<StockPriceHistory> getStockPriceHistory(String symbol)
  {
    return stockPriceHistoryDAO.getStockPriceHistoryBySymbol(symbol);
  }
}
