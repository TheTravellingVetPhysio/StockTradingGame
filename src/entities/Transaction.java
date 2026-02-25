package entities;

import java.time.LocalDateTime;
import java.util.UUID;

public class Transaction
{
  private final String id;
  private final String portfolioId;
  private final String stockSymbol;
  private final TransactionType type;
  private final int quantity;
  private final double pricePerShare;
  private final double totalAmount;
  private final double fee;
  private final LocalDateTime timestamp;

  private Transaction(String id, String portfolioId, String stockSymbol,
      TransactionType type, int quantity, double pricePerShare,
      double totalAmount, double fee, LocalDateTime timestamp)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.type = type;
    this.quantity = quantity;
    this.pricePerShare = pricePerShare;
    this.totalAmount = totalAmount;
    this.fee = fee;
    this.timestamp = timestamp;
  }

  public static Transaction createNew(String portfolioId, String stockSymbol,
      TransactionType type, int quantity, double pricePerShare,
      double totalAmount, double fee, LocalDateTime timestamp)
  {
    return new Transaction(UUID.randomUUID().toString(), portfolioId, stockSymbol, type, quantity, pricePerShare, totalAmount, fee, timestamp);
  }

  public static Transaction reloadFromStorage(String id, String portfolioId, String stockSymbol,
      TransactionType type, int quantity, double pricePerShare,
      double totalAmount, double fee, LocalDateTime timestamp)
  {
    return new Transaction(id, portfolioId, stockSymbol, type, quantity, pricePerShare, totalAmount, fee, timestamp);
  }

  public String getId()
  {
    return id;
  }

  public String getPortfolioId()
  {
    return portfolioId;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public TransactionType getType()
  {
    return type;
  }

  public int getQuantity()
  {
    return quantity;
  }

  public double getPricePerShare()
  {
    return pricePerShare;
  }

  public double getTotalAmount()
  {
    return totalAmount;
  }

  public double getFee()
  {
    return fee;
  }

  public LocalDateTime getTimestamp()
  {
    return timestamp;
  }
}
