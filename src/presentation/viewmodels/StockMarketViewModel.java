package presentation.viewmodels;

import business.events.StockUpdateEvent;
import business.observertooling.EventType;
import business.observertooling.Listener;
import business.services.StockListenerService;
import business.services.StockMarketService;
import business.services.TransactionService;
import entities.Stock;
import entities.StockPriceHistory;
import javafx.application.Platform;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import presentation.models.StockUI;

import java.util.*;
import java.util.stream.Collectors;

public class StockMarketViewModel implements Listener
{
  private StockMarketService stockMarketService;
  private TransactionService transactionService;
  private ObservableList<StockUI> stocksUI = FXCollections.observableArrayList();

  private long lastUpdate = 0;

  public StockMarketViewModel(StockMarketService stockMarketService,
      TransactionService transactionService,
      StockListenerService stockListenerService)
  {
    this.stockMarketService = stockMarketService;
    this.transactionService = transactionService;

    stockMarketService.getAllStocks().forEach(stock -> {
      stocksUI.add(new StockUI(stock));
    });

    stocksUI.stream().limit(5)
        .forEach(stockUI -> stockUI.setSelected(true));

    stockListenerService.addListener(EventType.STOCK_UPDATED, this);
  }

  @Override public void update(Object arg)
  {
    if (arg instanceof StockUpdateEvent event)
    {

      long now = System.currentTimeMillis();
      if (now - lastUpdate < 100) return;  // throttle til max 10 updates/sek
      lastUpdate = now;

      Platform.runLater(() -> {

        stocksUI.stream()
            .filter(stockUI -> stockUI.getSymbol().equals(event.symbol()))
            .findFirst().ifPresent(stockUI -> {
              stockUI.priceProperty().set(event.price());
              stockUI.stateProperty().set(event.state());
            });
      });
    }
  }

  public ObservableList<StockUI> getStocksUI()
  {
    return stocksUI;
  }

  public List<StockPriceHistory> getPriceHistory(String symbol)
  {
    return stockMarketService.getStockPriceHistory(symbol);
  }

}
