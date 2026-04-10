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
  private final Map<String, XYChart.Series<Number, Number>> seriesMap = new HashMap<>();
  private final ObservableList<XYChart.Series<Number, Number>> selectedSeries =
      FXCollections.observableArrayList();
  private final Map<String, Long> lastUpdatePerSymbol = new HashMap<>();

  private int tickRange = 30;

  public StockMarketViewModel(StockMarketService stockMarketService,
      TransactionService transactionService,
      StockListenerService stockListenerService)
  {
    this.stockMarketService = stockMarketService;
    this.transactionService = transactionService;

    stockMarketService.getAllStocks().forEach(stock -> stocksUI.add(new StockUI(stock)));

    stocksUI.forEach(ui -> {
      XYChart.Series<Number, Number> series = new XYChart.Series<>();
      series.setName(ui.getSymbol());
      seriesMap.put(ui.getSymbol(), series);
    });

    stocksUI.stream().limit(5).forEach(stockUI -> stockUI.setSelected(true));

    stockListenerService.addListener(EventType.STOCK_UPDATED, this);
  }

  @Override public void update(Object arg)
  {
    if (arg instanceof StockUpdateEvent event)
    {

      long now = System.currentTimeMillis();
      long last = lastUpdatePerSymbol.getOrDefault(event.symbol(), 0L);
      if (now - last < 100)
        return;  // throttle til max 10 updates/sek
      lastUpdatePerSymbol.put(event.symbol(), now);

      Platform.runLater(() -> {

        stocksUI.stream()
            .filter(stockUI -> stockUI.getSymbol().equals(event.symbol()))
            .findFirst()
            .ifPresent(stockUI -> {
              stockUI.priceProperty().set(event.price());
              stockUI.stateProperty().set(event.state());
              updateSeriesFor(stockUI.getSymbol());
            });
      });
    }
  }

  // LineChart
  public void refreshSelectedSeries() {
    selectedSeries.clear();
    stocksUI.stream()
        .filter(StockUI::isSelected)
        .map(ui -> seriesMap.get(ui.getSymbol()))
        .forEach(selectedSeries::add);
  }

  public ObservableList<XYChart.Series<Number, Number>> getSelectedSeries() {
    return selectedSeries;
  }

  private void updateSeriesFor(String symbol) {
    XYChart.Series<Number, Number> series = seriesMap.get(symbol);
    if (series == null) return;

    var fullHistory = stockMarketService.getStockPriceHistory(symbol);
    int size = fullHistory.size();
    var history = fullHistory.subList(Math.max(0, size - tickRange), size);

    series.getData().clear();
    int index = 0;
    for (StockPriceHistory h : history) {
      series.getData().add(new XYChart.Data<>(index++, h.getPrice()));
    }
  }

  public void setTickRange(int ticks)
  {
    this.tickRange = ticks;
  }

  public int getTickRange()
  {
    return tickRange;
  }

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
