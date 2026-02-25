package persistance.fileimplementation;

import entities.Portfolio;
import entities.Stock;
import persistance.interfaces.StockDAO;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class FileStockDAO implements StockDAO
{
  FileUnitOfWork uow;

  public FileStockDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void createStock(Stock stock)
  {
    uow.getStockList().add(stock);
  }

  @Override public void updateStock(Stock stock)
  {
    List<Stock> stocks = uow.getStockList();
    for (int i = 0; i < stocks.size(); i++)
    {
      if (stocks.get(i).getSymbol().equals(stock.getSymbol()))
      {
        stocks.set(i, stock);
        return;
      }
    }
    Logger.getInstance()
        .log("ERROR", "Stock not found: " + stock.getSymbol());
    throw new RuntimeException("Stock not found: " + stock.getSymbol());
  }

  @Override public void deleteStock(String stockSymbol)
  {
    List<Stock> stocks = uow.getStockList();
    boolean removed = stocks.removeIf(stock -> stock.getSymbol().equals(stockSymbol));
    if (!removed)
    {
      Logger.getInstance().log("ERROR", "Stock not found: " + stockSymbol);
      throw new RuntimeException("Stock not found: " + stockSymbol);
    }
  }

  @Override public Stock getStockBySymbol(String stockSymbol)
  {
    List<Stock> stocks = uow.getStockList();
    for (Stock stock : stocks)
    {
      if (stock.getSymbol().equals(stockSymbol))
      {
        return stock;
      }
    }
    Logger.getInstance().log("ERROR", "Stock not found: " + stockSymbol);
    throw new RuntimeException("Stock not found: " + stockSymbol);
  }

  @Override public List<Stock> getAllStocks()
  {
    List<Stock> stocks = uow.getStockList();
    List<Stock> stocksCopy = new ArrayList<>();
    for (Stock stock  : stocks)
    {
      stocksCopy.add(
          new Stock(stock.getSymbol(), stock.getName(),
              stock.getCurrentPrice(), stock.getCurrentState()));
    }
    return stocksCopy;
  }
}