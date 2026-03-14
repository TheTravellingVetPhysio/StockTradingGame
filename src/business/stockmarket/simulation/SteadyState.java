package business.stockmarket.simulation;

import java.util.Random;

public class SteadyState implements StockState
{
  public static final String NAME = "SteadyState";
  private static final Random random = new Random();

  @Override
  public double calculatePriceChange(LiveStock stock)
  {
    double changePercent = (random.nextDouble() * 2 - 1) / 100;
    double change = stock.getCurrentPrice() * changePercent;

    TransitionManager.getInstance().transition(stock, changePercent);

    return change;
  }

  @Override
  public String getName()
  {
    return NAME;
  }
}