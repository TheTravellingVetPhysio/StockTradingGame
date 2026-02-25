package entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class StockPriceHistory
{
  private final String id;
  private final String stockSymbol;
  private final double price;
  private final LocalDateTime timestamp;

  private StockPriceHistory(String id, String stockSymbol, double price,
      LocalDateTime timestamp)
  {
    this.id = id;
    this.stockSymbol = stockSymbol;
    this.price = price;
    this.timestamp = timestamp;
  }

  public static StockPriceHistory createNew(String stockSymbol, double price,
      LocalDateTime timestamp) {
    return new StockPriceHistory(UUID.randomUUID().toString(), stockSymbol, price, timestamp);
  }

  public static StockPriceHistory reloadFromStorage(String id, String stockSymbol, double price,
      LocalDateTime timestamp) {
    return new StockPriceHistory(id, stockSymbol, price, timestamp);
  }

  public String getId()
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
