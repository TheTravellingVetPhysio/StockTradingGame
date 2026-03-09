package business.stockmarket.simulation;

import java.util.Random;

public class BankruptState implements StockState
{
  private int ticks = 0;
  private final int bankruptDuration;

  public BankruptState()
  {
    Random random = new Random();
    this.bankruptDuration = random.nextInt(21) + 5;
  }

  @Override public double calculatePriceChange(LiveStock stock)
  {
    ticks++;

    if (ticks >= bankruptDuration)
    {
      stock.setState(new SteadyState());
      logTransition(stock);
    }
    return 0;
  }

  @Override public String getName()
  {
    return "BankruptState";
  }
}
