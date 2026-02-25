package entities;

import java.util.UUID;

public class OwnedStock
{
  private final String id;
  private final String portfolioId;
  private final String stockSymbol;
  private int numberOfShares;

  private OwnedStock(String id, String portfolioId, String stockSymbol,
      int numberOfShares)
  {
    this.id = id;
    this.portfolioId = portfolioId;
    this.stockSymbol = stockSymbol;
    this.numberOfShares = numberOfShares;
  }

  // skal dubletter - samme portfolioId og samme aktie - lægges sammen under samme id? Eller repræsenterer denne de forskellige "batches" de er købt i? Og hvis det repræsenterer batches, giver det så mening med en setNumberOfShares?
  public static OwnedStock createNew(String portfolioId, String stockSymbol,
      int numberOfShares)
  {
    return new OwnedStock(UUID.randomUUID().toString(), portfolioId,
        stockSymbol, numberOfShares);
  }

  public static OwnedStock reloadFromStorage(String id, String portfolioId,
      String stockSymbol, int numberOfShares)
  {
    return new OwnedStock(id, portfolioId, stockSymbol, numberOfShares);
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

  public int getNumberOfShares()
  {
    return numberOfShares;
  }

  public void setNumberOfShares(int sharesTransaction)
  {
    this.numberOfShares = numberOfShares + sharesTransaction;
  }
}
