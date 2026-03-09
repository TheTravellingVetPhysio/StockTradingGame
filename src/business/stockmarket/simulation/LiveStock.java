package business.stockmarket.simulation;

import shared.configuration.AppConfig;

public class LiveStock
{
  private String symbol;
  private StockState currentState;
  private double currentPrice;

  private LiveStock(String symbol, StockState currentState, double currentPrice)
  {
    this.symbol = symbol;
    this.currentState = currentState;
    this.currentPrice = currentPrice;
  }

  public static LiveStock createNew(String symbol)
  {
    return new LiveStock(symbol, StockState.ACTIVE,
        AppConfig.getInstance().getDefaultStockPrice());
  }

  public static LiveStock reloadFromStorage(String symbol, String stateName,
      double currentPrice)
  {
    return new LiveStock(symbol, mapState(stateName), currentPrice);
  }

  public void updatePrice()
  {
    double priceChange = currentState.calculatePriceChange(this);
    currentPrice += priceChange;

    if (currentPrice <= 0)
    {
      currentPrice = 0;
      setState(new BankruptState());
    }
  }

  protected void setState(StockState newState)
  {
    this.currentState = newState;
  }

  private static StockState mapState(String stateName)
  {
    return switch (stateName)
    {
      case "Stable" -> new StableState();
      case "Bankrupt" -> new BankruptState();
      // osv...
      default ->
          throw new IllegalArgumentException("Unknown state: " + stateName);
    };
  }

  public String getSymbol()
  {
    return symbol;
  }

  public double getCurrentPrice()
  {
    return currentPrice;
  }

  public String getCurrentStateName()
  {
    return currentState.getCurrentState();
  }
}
