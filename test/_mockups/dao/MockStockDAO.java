package _mockups.dao;

import entities.Stock;
import persistance.fileimplementation.FileUnitOfWork;
import persistance.interfaces.StockDAO;
import shared.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class MockStockDAO implements StockDAO
{
  private List<Stock> stocks = new ArrayList<>();

  @Override public void createStock(Stock stock)
  {
    stocks.add(stock);
  }

  @Override public void updateStock(Stock stock)
  {
    for (int i = 0; i < stocks.size(); i++)
    {
      if (stocks.get(i).getSymbol().equals(stock.getSymbol()))
      {
        stocks.set(i, stock);
        return;
      }
    }
    throw new NotFoundException("Stock not found: " + stock.getSymbol());
  }

  @Override public void deleteStock(String stockSymbol)
  {
    boolean removed = stocks.removeIf(
        stock -> stock.getSymbol().equals(stockSymbol));
    if (!removed)
    {
      throw new NotFoundException("Stock not found: " + stockSymbol);
    }
  }

  @Override public Stock getStockBySymbol(String stockSymbol)
  {
    for (Stock stock : stocks)
    {
      if (stock.getSymbol().equals(stockSymbol))
      {
        return stock;
      }
    }
    throw new NotFoundException("Stock not found: " + stockSymbol);
  }

  @Override public List<Stock> getAllStocks()
  {
    List<Stock> stocksCopy = new ArrayList<>();
    for (Stock stock : stocks)
    {
      stocksCopy.add(
          new Stock(stock.getSymbol(), stock.getName(), stock.getCurrentPrice(),
              stock.getCurrentState(), stock.getRiskProfile()));
    }
    return stocksCopy;
  }

}
