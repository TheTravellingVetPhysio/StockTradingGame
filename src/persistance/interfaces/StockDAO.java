package persistance.interfaces;

import entities.Portfolio;
import entities.Stock;

import java.util.List;

public interface StockDAO
{
  void createStock(Stock stock);
  void updateStock(Stock stock);
  void deleteStock(String stockSymbol);
  Stock getStockBySymbol(String stockSymbol);
  List<Stock> getAllStocks();
}
