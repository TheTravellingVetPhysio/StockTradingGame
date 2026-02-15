package entities;

import java.time.LocalDateTime;

public class StockPriceHistory
{
  private final int id;
  private final String stockSymbol;
  private final double price;
  private final LocalDateTime timestamp;

  private static int idCounter = 0;

  public StockPriceHistory(String stockSymbol, double price,
      LocalDateTime timestamp)
  {
    id = idCounter++;
    this.stockSymbol = stockSymbol;
    this.price = price;
    this.timestamp = timestamp;
  }

  public int getId()
  {
    return id;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public double getPrice()
  {
    return price;
  }

  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }
}
