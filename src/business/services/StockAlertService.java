package business.services;

import business.events.StockAlertEvent;
import business.events.StockBankruptEvent;
import business.events.StockUpdateEvent;
import business.observertooling.EventType;
import business.observertooling.Listener;
import business.observertooling.Subject;
import business.stockmarket.simulation.BankruptState;
import business.stockmarket.simulation.DecliningState;
import business.stockmarket.simulation.ResetState;
import shared.logging.Logger;

import java.util.HashSet;
import java.util.Set;

public class StockAlertService extends Subject implements Listener
{

  private final Set<String> decliningStocks = new HashSet<>();
  private final Set<String> bankruptStocks = new HashSet<>();

  @Override
  public void update(Object arg)
  {
    if (arg instanceof StockBankruptEvent event)
    {
      handleBankrupt(event);
    }
    else if (arg instanceof StockUpdateEvent event)
    {
      handleUpdate(event);
    }
  }

  private void handleBankrupt(StockBankruptEvent event)
  {
    bankruptStocks.add(event.symbol());
    Logger.getInstance().log("WARN", "Alert: " + event.symbol() + " has gone bankrupt!");
    notifyListeners(EventType.STOCK_ALERT, new StockAlertEvent(event.symbol(), BankruptState.NAME));
  }

  private void handleUpdate(StockUpdateEvent event)
  {
    if (event.state().equals(ResetState.NAME) && bankruptStocks.contains(event.symbol()))
    {
      bankruptStocks.remove(event.symbol());
      Logger.getInstance().log("INFO", "Alert: " + event.symbol() + " has reset and is available again!");
      notifyListeners(EventType.STOCK_ALERT, new StockAlertEvent(event.symbol(), ResetState.NAME));
    }

    if (event.state().equals(DecliningState.NAME))
    {
      if (!decliningStocks.contains(event.symbol()))
      {
        decliningStocks.add(event.symbol());
        Logger.getInstance().log("WARN", "Alert: " + event.symbol() + " is declining!");
        notifyListeners(EventType.STOCK_ALERT, new StockAlertEvent(event.symbol(), DecliningState.NAME));
      }
    }
    else
    {
      decliningStocks.remove(event.symbol());
    }
  }
}
