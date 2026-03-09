package business.stockmarket;

import business.stockmarket.simulation.LiveStock;
import entities.Stock;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

public class StockMarket
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
      Logger.getInstance().log("INFO", liveStock.getSymbol() +
          " | Price: " + liveStock.getCurrentPrice() +
          " | State: " + liveStock.getCurrentStateName());
    }
  }

  public Stream<LiveStock> getLiveStocks() {
    return liveStockList.stream();
  }
}
