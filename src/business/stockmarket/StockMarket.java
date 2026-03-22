package business.stockmarket;

import business.events.StockBankruptEvent;
import business.events.StockUpdateEvent;
import business.observertooling.EventType;
import business.observertooling.Subject;
import business.stockmarket.simulation.LiveStock;
import entities.Stock;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class StockMarket extends Subject
{
  private static final StockMarket instance = new StockMarket(); // Eager initialization
  private final List<LiveStock> liveStockList = new ArrayList<>();

  private StockMarket()
  {
  }

  public static StockMarket getInstance()
  {
    return instance;
  }

  public void addNewStock(String symbol, String name, double riskProfile)
  {
    liveStockList.add(LiveStock.createNew(symbol, name, riskProfile));
  }

  public void addExistingStock(Stock stock)
  {
    liveStockList.add(
        LiveStock.reloadFromStorage(stock.getSymbol(), stock.getName(),
            stock.getCurrentState(), stock.getCurrentPrice(), stock.getRiskProfile()));
  }

  public void updateAllStocks()
  {
    for (LiveStock liveStock : liveStockList)
    {
      liveStock.updatePrice();

      notifyListeners(EventType.STOCK_UPDATED,
          new StockUpdateEvent(liveStock.getSymbol(), liveStock.getName(),
              liveStock.getCurrentPrice(), liveStock.getCurrentStateName()));

      if (liveStock.getJustWentBankrupt())
      {
        notifyListeners(EventType.STOCK_BANKRUPT,
            new StockBankruptEvent(liveStock.getSymbol()));
      }
      /*
      Alternativ er at lave LiveStock til Subject og StockMarket til listener.
      Så ville flowet blive:
      -> LiveStock (Subject)
        -> notifyListeners(STOCK_BANKRUPT, event)
          -> StockMarket (Listener) modtager det
            -> notifyListeners(STOCK_BANKRUPT, event)
              -> andre listeners modtager det
      */

      Logger.getInstance().log("INFO",
          liveStock.getSymbol() + " | Price: " + liveStock.getCurrentPrice()
              + " | State: " + liveStock.getCurrentStateName());
    }
  }

}
