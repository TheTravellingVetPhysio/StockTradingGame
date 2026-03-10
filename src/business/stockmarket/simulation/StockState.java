package business.stockmarket.simulation;

import shared.logging.Logger;

public interface StockState
{
  double calculatePriceChange(LiveStock stock);
  String getName();
}
