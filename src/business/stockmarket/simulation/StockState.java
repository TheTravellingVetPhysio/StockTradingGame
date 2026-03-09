package business.stockmarket.simulation;

public interface StockState
{
  double calculatePriceChange(LiveStock stock);
  String getName();
}
