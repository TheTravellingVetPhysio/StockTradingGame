package business.stockmarket.simulation;

public class BankruptState implements StockState
{
  public static final String NAME = "BankruptState";

  @Override
  public double calculatePriceChange(LiveStock stock)
  {
    TransitionManager.getInstance().transition(stock, 0);
    return 0;
  }

  @Override
  public String getName()
  {
    return NAME;
  }
}