package business.stockmarket.simulation;

import shared.logging.Logger;

public interface StockState
{
  double calculatePriceChange(LiveStock stock);
  String getName();

  default void logTransition(LiveStock stock) {
    Logger.getInstance().log("INFO",
        stock.getSymbol() + " transitioning from " + this.getName()
            + " to " + stock.getCurrentStateName());
  }
}
