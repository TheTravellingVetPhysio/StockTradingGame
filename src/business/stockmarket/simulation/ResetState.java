package business.stockmarket.simulation;

import shared.configuration.AppConfig;

public class ResetState implements StockState
{
  @Override public double calculatePriceChange(LiveStock stock)
  {
    stock.setState(new SteadyState());
    return AppConfig.getInstance().getDefaultStockPrice();
  }

  @Override public String getName()
  {
    return "ResetState";
  }
}
