package business.stockmarket.simulation;

import java.util.Random;

public class GrowingState implements StockState
{
  private static final Random random = new Random();
  private int consecutiveDeclines = 0;

  @Override public double calculatePriceChange(LiveStock stock)
  {
    double changePercent = (random.nextDouble() * 3 - 1) / 100;
    double change = stock.getCurrentPrice() * changePercent;
    double rand = random.nextDouble();

    if (changePercent < 0)
    {
      consecutiveDeclines++;
    }
    else
    {
      consecutiveDeclines = 0;
    }

    if (consecutiveDeclines >= 3)
    {
      if (rand < 0.6)
      {
        stock.setState(new SteadyState());
      }
      else if (rand < 0.85)
      {
        stock.setState(new DecliningState());
      }
    }
    else if (consecutiveDeclines >= 2)
    {
      if (rand < 0.35)
      {
        stock.setState(new SteadyState());
      }
      else if (rand < 0.5)
      {
        stock.setState(new DecliningState());
      }
    }
    else if (consecutiveDeclines >= 1)
    {
      if (rand < 0.07)
      {
        stock.setState(new SteadyState());
      }
      else if (rand < 0.1)
      {
        stock.setState(new DecliningState());
      }
    }

    return change;
  }

  @Override public String getName()
  {
    return "GrowingState";
  }
}
