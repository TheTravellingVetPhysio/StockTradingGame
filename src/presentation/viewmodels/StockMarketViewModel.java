package presentation.viewmodels;

import business.events.StockUpdateEvent;
import business.observertooling.EventType;
import business.observertooling.Listener;
import business.services.StockListenerService;
import business.services.StockMarketService;
import business.services.TransactionService;
import entities.StockPriceHistory;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import presentation.models.StockUI;

import java.util.*;

public class StockMarketViewModel implements Listener
{
  private StockMarketService stockMarketService;
  private TransactionService transactionService;
  private ObservableList<StockUI> stocksUI = FXCollections.observableArrayList();

  private long lastUpdate = 0;
  private int tickRange = 30;
  private Runnable onChartDataUpdated = () -> {};


  public StockMarketViewModel(StockMarketService stockMarketService,
      TransactionService transactionService,
      StockListenerService stockListenerService)
  {
    this.stockMarketService = stockMarketService;
    this.transactionService = transactionService;

    stockMarketService.getAllStocks().forEach(stock -> {
      stocksUI.add(new StockUI(stock));
    });

    stocksUI.stream().limit(5).forEach(stockUI -> stockUI.setSelected(true));

    stockListenerService.addListener(EventType.STOCK_UPDATED, this);
  }

  @Override public void update(Object arg)
  {
    if (arg instanceof StockUpdateEvent event)
    {

      long now = System.currentTimeMillis();
      if (now - lastUpdate < 100)
        return;  // throttle til max 10 updates/sek
      lastUpdate = now;

      Platform.runLater(() -> {

        stocksUI.stream()
            .filter(stockUI -> stockUI.getSymbol().equals(event.symbol()))
            .findFirst().ifPresent(stockUI -> {
              stockUI.priceProperty().set(event.price());
              stockUI.stateProperty().set(event.state());
            });
        onChartDataUpdated.run();
      });
    }
  }

  // LineChart

  public XYChart.Series<Number, Number> buildSeriesFor(String stockSymbol)
  {
    var fullHistory = getPriceHistory(stockSymbol);
    int size = fullHistory.size();

    List<StockPriceHistory> history = fullHistory.subList(
        Math.max(0, size - tickRange), size);

    XYChart.Series<Number, Number> series = new XYChart.Series<>();
    series.setName(stockSymbol);

    int index = 0;
    for (StockPriceHistory h : history) {
      series.getData().add(new XYChart.Data<>(index++, h.getPrice()));
    }

    return series;
  }

  public List<XYChart.Series<Number, Number>> buildSelectedSeries() {
    return stocksUI.stream()
        .filter(StockUI::isSelected)
        .map(ui -> buildSeriesFor(ui.getSymbol()))
        .toList();
  }

  public void setTickRange(int ticks)
  {
    this.tickRange = ticks;
  }

  public int getTickRange()
  {
    return tickRange;
  }

  public void setOnChartDataUpdated(Runnable r) { this.onChartDataUpdated = r; }

  // Getters

  public ObservableList<StockUI> getStocksUI()
  {
    return stocksUI;
  }

  public List<StockPriceHistory> getPriceHistory(String symbol)
  {
    return stockMarketService.getStockPriceHistory(symbol);
  }

}
