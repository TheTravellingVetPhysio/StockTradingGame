package presentation.viewmodels;

import business.events.StockUpdateEvent;
import business.observertooling.EventType;
import business.observertooling.Listener;
import business.services.StockListenerService;
import business.services.StockMarketService;
import business.services.TransactionService;
import dto.StockBuyRequest;
import dto.StockSellRequest;
import dto.TransactionResult;
import entities.Portfolio;
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
  private MainViewModel mainViewModel;
  private ObservableList<StockUI> stocksUI = FXCollections.observableArrayList();
  private final Map<String, XYChart.Series<Number, Number>> seriesMap = new HashMap<>();
  private final ObservableList<XYChart.Series<Number, Number>> selectedSeries = FXCollections.observableArrayList();
  private final Map<String, Long> lastUpdatePerSymbol = new HashMap<>();
  private final Map<String, List<Double>> priceHistory = new HashMap<>();

  private int tickRange = 30;

  public StockMarketViewModel(StockMarketService stockMarketService,
      TransactionService transactionService,
      StockListenerService stockListenerService, MainViewModel mainViewModel)
  {
    this.stockMarketService = stockMarketService;
    this.transactionService = transactionService;
    this.mainViewModel = mainViewModel;

    stockMarketService.getAllStocks()
        .forEach(stock -> stocksUI.add(new StockUI(stock)));

    stocksUI.forEach(ui -> {
      XYChart.Series<Number, Number> series = new XYChart.Series<>();
      series.setName(ui.getSymbol());
      seriesMap.put(ui.getSymbol(), series);

      // Load pris-historik in-memory
      List<Double> prices = new ArrayList<>();
      stockMarketService.getStockPriceHistory(ui.getSymbol())
          .forEach(h -> prices.add(h.getPrice()));
      priceHistory.put(ui.getSymbol(), prices);
    });

    stocksUI.stream().limit(5).forEach(stockUI -> stockUI.setSelected(true));
    seriesMap.keySet().forEach(this::rebuildSeriesFrom);

    stockListenerService.addListener(EventType.STOCK_UPDATED, this);
  }

  @Override public void update(Object arg)
  {
    if (arg instanceof StockUpdateEvent event)
    {
      long now = System.currentTimeMillis();
      long last = lastUpdatePerSymbol.getOrDefault(event.symbol(), 0L);
      if (now - last < 100)
        return;
      lastUpdatePerSymbol.put(event.symbol(), now);

      Platform.runLater(() -> {
        // Opdater tabel
        stocksUI.stream().filter(ui -> ui.getSymbol().equals(event.symbol()))
            .findFirst().ifPresent(ui -> {
              ui.priceProperty().set(event.price());
              ui.stateProperty().set(event.state());
            });

        List<Double> prices = priceHistory.get(event.symbol());
        if (prices != null)
        {
          prices.add(event.price());
          rebuildSeriesFrom(event.symbol());
        }
      });
    }
  }

  // LineChart
  private void rebuildSeriesFrom(String symbol)
  {
    XYChart.Series<Number, Number> series = seriesMap.get(symbol);
    List<Double> prices = priceHistory.get(symbol);
    if (series == null || prices == null)
      return;

    int size = prices.size();
    List<Double> window = prices.subList(Math.max(0, size - tickRange), size);

    series.getData().clear();
    for (int i = 0; i < window.size(); i++)
    {
      series.getData().add(new XYChart.Data<>(i, window.get(i)));
    }
  }

  public void refreshSelectedSeries()
  {
    List<XYChart.Series<Number, Number>> toShow = stocksUI.stream()
        .filter(StockUI::isSelected)
        .map(stockUI -> seriesMap.get(stockUI.getSymbol())).toList();

    selectedSeries.removeIf(stockUI -> !toShow.contains(stockUI));
    toShow.stream().filter(stockUI -> !selectedSeries.contains(stockUI))
        .forEach(selectedSeries::add);
  }

  public ObservableList<XYChart.Series<Number, Number>> getSelectedSeries()
  {
    return selectedSeries;
  }

  public void setTickRange(int ticks)
  {
    this.tickRange = ticks;
    seriesMap.keySet().forEach(this::rebuildSeriesFrom);
  }

  // Getters

  public ObservableList<StockUI> getStocksUI()
  {
    return stocksUI;
  }

  public TransactionResult buyStock(String symbol, int amount)
  {
    Portfolio portfolio = mainViewModel.getSelectedPortfolio();
    if (portfolio == null)
    {
      return new TransactionResult(false, "No portfolio chosen.");
    }
    try
    {
      transactionService.buyStock(
          new StockBuyRequest(portfolio.getId(), symbol, amount));
      return new TransactionResult(true, "Purchase of " + amount + " shares of " + symbol + " was successful.");
    }
    catch (IllegalArgumentException | IllegalStateException e)
    {
      return new TransactionResult(false, e.getMessage());
    }
    catch (Exception e)
    {
      return new TransactionResult(false, "An unexpected error occurred: " + e.getMessage());
    }
  }

  public TransactionResult sellStock(String symbol, int amount)
  {
    Portfolio portfolio = mainViewModel.getSelectedPortfolio();
    if (portfolio == null)
    {
      return new TransactionResult(false, "No portfolio chosen.");
    }
    try
    {
      transactionService.sellStock(
          new StockSellRequest(portfolio.getId(), symbol, amount));
      return new TransactionResult(true, "Sale of " + amount + " shares of " + symbol + " was successful.");
    }
    catch (IllegalArgumentException | IllegalStateException e)
    {
      return new TransactionResult(false, e.getMessage());
    }
    catch (Exception e)
    {
      return new TransactionResult(false, "An unexpected error occurred: " + e.getMessage());
    }
  }
}
