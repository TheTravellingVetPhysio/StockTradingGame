package business.stockmarket.simulation;

import shared.configuration.AppConfig;
import shared.logging.Logger;

import java.util.Random;

public class LiveStock
{
  private final String symbol;
  private final String name;
  private StockState currentState;
  private double currentPrice;

  private LiveStock(String symbol, String name, StockState currentState, double currentPrice)
  {
    this.symbol = symbol;
    this.name = name;
    this.currentState = currentState;
    this.currentPrice = currentPrice;
  }

  public static LiveStock createNew(String symbol, String name)
  {
    Random random = new Random();
    return new LiveStock(symbol, name, new SteadyState(),
        AppConfig.getInstance().getDefaultStockPrice()
            + random.nextDouble() * 100);
  }

  public static LiveStock reloadFromStorage(String symbol, String name, String stateName,
      double currentPrice)
  {
    return new LiveStock(symbol, name, mapState(stateName), currentPrice);
  }

  public boolean updatePrice()
  {
    String stateBefore = getCurrentStateName();

    double priceChange = currentState.calculatePriceChange(this);
    currentPrice += priceChange;

    if (currentPrice <= 0)
    {
      currentPrice = 0;
      setState(new BankruptState());
      Logger.getInstance().log("WARN", symbol + " has gone bankrupt!");
    }

    return !stateBefore.equals(BankruptState.NAME) && getCurrentStateName().equals(BankruptState.NAME);
  }

  protected void setState(StockState newState)
  {
    this.currentState = newState;
  }

  private static StockState mapState(String stateName)
  {
    return switch (stateName)
    {
      case SteadyState.NAME -> new SteadyState();
      case GrowingState.NAME -> new GrowingState();
      case DecliningState.NAME -> new DecliningState();
      case BankruptState.NAME -> new BankruptState();
      case ResetState.NAME -> new ResetState();
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

  public String getName()
  {
    return name;
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
