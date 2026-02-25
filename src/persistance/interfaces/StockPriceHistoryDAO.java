package persistance.interfaces;

import entities.StockPriceHistory;
import java.util.List;

public interface StockPriceHistoryDAO
{
  void createStockPriceHistory(StockPriceHistory stockPriceHistory);
  void appendStockPriceHistory(StockPriceHistory stockPriceHistory);
  StockPriceHistory getStockPriceHistoryById(String id);
  List<StockPriceHistory> getStockPriceHistoryBySymbol(String stockSymbol);
  List<StockPriceHistory> getAllStockPriceHistory();
}