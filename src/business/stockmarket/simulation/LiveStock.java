package business.stockmarket.simulation;

import shared.configuration.AppConfig;
import shared.logging.Logger;

import java.util.Random;

public class LiveStock
{
  private final String symbol;
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
    Random random = new Random();
    return new LiveStock(symbol, new SteadyState(),
        AppConfig.getInstance().getDefaultStockPrice()
            + random.nextDouble() * 100);
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
      Logger.getInstance().log("WARN", symbol + " has gone bankrupt!");

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
      case "SteadyState" -> new SteadyState();
      case "GrowingState" -> new GrowingState();
      case "DecliningState" -> new DecliningState();
      case "BankruptState" -> new BankruptState();
      case "ResetState" -> new ResetState();
      default ->
      {
        Logger.getInstance().log("ERROR", "Unknown state: " + stateName);
        throw new IllegalArgumentException("Unknown state: " + stateName);
      }
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
    return currentState.getName();
  }
}
