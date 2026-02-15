package entities;

public class OwnedStock
{
  private final int id;
  private final int portfolioId;
  private final String stockSymbol;
  private int numberOfShares;

  private static int idCounter = 0;

  public OwnedStock(int portfolioId, String stockSymbol,
      int numberOfShares)
  {
    this.portfolioId = portfolioId;
    this.id = idCounter++;
    this.stockSymbol = stockSymbol;
    this.numberOfShares = numberOfShares;
  }

  public int getId()
  {
    return id;
  }

  public int getPortfolioId()
  {
    return portfolioId;
  }

  public String getStockSymbol()
  {
    return stockSymbol;
  }

  public int getNumberOfShares()
  {
    return numberOfShares;
  }

  public void setNumberOfShares(int sharesTransaction)
  {
    this.numberOfShares = numberOfShares + sharesTransaction;
  }
}
