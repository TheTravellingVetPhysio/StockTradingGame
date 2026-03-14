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
import java.util.stream.Stream;

public class StockMarket extends Subject
{
  private static final StockMarket instance = new StockMarket(); // Eager initialization
  private final List<LiveStock> liveStockList = new ArrayList<>();

  private StockMarket() {}

  public static StockMarket getInstance() {
    return instance;
  }

  public void addNewStock(String symbol) {
    liveStockList.add(LiveStock.createNew(symbol));
  }

  public void addExistingStock(Stock stock) {
    liveStockList.add(LiveStock.reloadFromStorage(
        stock.getSymbol(),
        stock.getCurrentState(),
        stock.getCurrentPrice()
    ));
  }

  public void updateAllStocks() {
    for (LiveStock liveStock : liveStockList) {
      liveStock.updatePrice();
      notifyListeners(EventType.STOCK_UPDATED, liveStock);

      if (liveStock.getCurrentStateName().equals("BankruptState")) {
        notifyListeners(EventType.STOCK_BANKRUPT, liveStock);
      }

      Logger.getInstance().log("INFO", liveStock.getSymbol() +
          " | Price: " + liveStock.getCurrentPrice() +
          " | State: " + liveStock.getCurrentStateName());
    }
  }

  public Stream<LiveStock> getLiveStocks() {
    return liveStockList.stream();
  }
}
