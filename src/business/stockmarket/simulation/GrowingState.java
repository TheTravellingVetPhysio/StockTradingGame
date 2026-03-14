package business.stockmarket.simulation;

import java.util.Random;

public class GrowingState implements StockState
{
  public static final String NAME = "GrowingState";
  private static final Random random = new Random();

  @Override
  public double calculatePriceChange(LiveStock stock)
  {
    double changePercent = (random.nextDouble() * 3 - 1) / 100;
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