package business.stockmarket.simulation;

public class BankruptState implements StockState
{
  @Override
  public double calculatePriceChange(LiveStock stock)
  {
    TransitionManager.getInstance().transition(stock, 0);
    return 0;
  }

  @Override
  public String getName()
  {
    return "BankruptState";
  }
}