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
  private boolean justWentBankrupt = false;
  private double riskProfile;

  private LiveStock(String symbol, String name, StockState currentState,
      double currentPrice, double riskProfile)
  {
    this.symbol = symbol;
    this.name = name;
    this.currentState = currentState;
    this.currentPrice = currentPrice;
    this.riskProfile = riskProfile;
  }

  public static LiveStock createNew(String symbol, String name, double riskProfile)
  {
    Random random = new Random();
    return new LiveStock(symbol, name, new SteadyState(),
        AppConfig.getInstance().getDefaultStockPrice()
            + random.nextDouble() * 100, riskProfile);
  }

  public static LiveStock reloadFromStorage(String symbol, String name,
      String stateName, double currentPrice, double riskProfile)
  {
    return new LiveStock(symbol, name, mapState(stateName), currentPrice, riskProfile);
  }

  public void updatePrice()
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

    if (!stateBefore.equals(BankruptState.NAME) && getCurrentStateName().equals(
        BankruptState.NAME)) {
    justWentBankrupt = true;
  }
    ;
  }

  public boolean getJustWentBankrupt() {
    return justWentBankrupt;
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

  public double getRiskProfile()
  {
    return riskProfile;
  }
}
