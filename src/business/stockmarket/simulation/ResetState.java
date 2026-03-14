package business.stockmarket.simulation;

import shared.configuration.AppConfig;

public class ResetState implements StockState
{
  public static final String NAME = "ResetState";

  @Override
  public double calculatePriceChange(LiveStock stock)
  {
    TransitionManager.getInstance().transition(stock, 0);
    return AppConfig.getInstance().getDefaultStockPrice();
  }

  @Override
  public String getName()
  {
    return NAME;
  }
}