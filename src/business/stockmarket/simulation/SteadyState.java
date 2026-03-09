package business.stockmarket.simulation;

import java.util.Random;

public class SteadyState implements StockState
{
  private static final Random random = new Random();

  @Override public double calculatePriceChange(LiveStock stock)
  {
    double changePercent = (random.nextDouble() * 2 - 1) / 100;
    double change = stock.getCurrentPrice() * changePercent;
    double rand = random.nextDouble();

    if (rand < 0.08) {
      stock.setState(new GrowingState());
      logTransition(stock);
    }
    else if (rand < 0.14) {
      stock.setState(new DecliningState());
      logTransition(stock);
    }
    return change;
  }

  @Override public String getName()
  {
    return "SteadyState";
  }
}
