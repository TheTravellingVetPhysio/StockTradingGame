package presentation.viewmodels;

import business.events.StockUpdateEvent;
import business.observertooling.EventType;
import business.observertooling.Listener;
import business.services.PortfolioService;
import business.services.StockListenerService;
import business.services.StockMarketService;
import business.services.TransactionService;
import dto.StockSellRequest;
import dto.TransactionResult;
import entities.OwnedStock;
import entities.Portfolio;
import javafx.application.Platform;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.chart.XYChart;
import presentation.models.OwnedStockUI;

import java.util.ArrayList;
import java.util.List;

public class DashboardViewModel implements Listener
{
  private final PortfolioService portfolioService;
  private final TransactionService transactionService;
  private final MainViewModel mainViewModel;
  private final StockMarketService stockMarketService;

  private final ObservableList<OwnedStockUI> ownedStocksUI = FXCollections.observableArrayList();
  private final DoubleProperty portfolioBalance = new SimpleDoubleProperty(0);
  private final DoubleProperty portfolioWorth = new SimpleDoubleProperty(0);
  private final StringProperty portfolioName = new SimpleStringProperty("");

  private final ObservableList<XYChart.Series<Number, Number>> worthSeries = FXCollections.observableArrayList();
  private final XYChart.Series<Number, Number> series = new XYChart.Series<>();
  private final List<Double> worthHistory = new ArrayList<>();

  public DashboardViewModel(PortfolioService portfolioService,
      TransactionService transactionService,
      StockMarketService stockMarketService,
      StockListenerService stockListenerService, MainViewModel mainViewModel)
  {
    this.portfolioService = portfolioService;
    this.transactionService = transactionService;
    this.mainViewModel = mainViewModel;
    this.stockMarketService = stockMarketService;

    series.setName("Portfolio value");
    worthSeries.add(series);

    mainViewModel.selectedPortfolioProperty()
        .addListener((obs, oldVal, newVal) -> {
          if (newVal != null)
            refresh();
        });

    if (mainViewModel.getSelectedPortfolio() != null)
      refresh();

    stockListenerService.addListener(EventType.STOCK_UPDATED, this);
  }

  @Override public void update(Object arg)
  {
    if (arg instanceof StockUpdateEvent)
    {
      Platform.runLater(this::refresh);
    }
  }

  public void refresh()
  {
    Portfolio portfolio = mainViewModel.getSelectedPortfolio();
    if (portfolio == null)
      return;

    String portfolioId = portfolio.getId();
    portfolioName.set(portfolio.getName());
    portfolioBalance.set(portfolioService.getPortfolioBalance(portfolioId));
    portfolioWorth.set(portfolioService.getPortfolioWorth(portfolioId));

    List<OwnedStock> owned = portfolioService.getOwnedStocks(portfolioId);
    ownedStocksUI.clear();
    for (OwnedStock os : owned)
    {
      double price = stockMarketService.getStock(os.getStockSymbol()).currentPrice();
      ownedStocksUI.add(
          new OwnedStockUI(os.getStockSymbol(), os.getNumberOfShares(),
              stockMarketService.getStock(os.getStockSymbol()).currentPrice()));
    }

    worthHistory.add(portfolioService.getPortfolioWorth(portfolioId));
    rebuildSeries();
  }

  private void rebuildSeries()
  {
    series.getData().clear();
    for (int i = 0; i < worthHistory.size(); i++)
      series.getData().add(new XYChart.Data<>(i, worthHistory.get(i)));
  }

  public TransactionResult sellStock(String symbol, int amount)
  {
    Portfolio portfolio = mainViewModel.getSelectedPortfolio();
    if (portfolio == null)
      return new TransactionResult(false, "No portfolio chosen.");
    try
    {
      transactionService.sellStock(
          new StockSellRequest(portfolio.getId(), symbol, amount));
      refresh();
      return new TransactionResult(true,
          "Sale of " + amount + " shares of " + symbol + " was successful.");
    }
    catch (IllegalArgumentException | IllegalStateException e)
    {
      return new TransactionResult(false, e.getMessage());
    }
    catch (Exception e)
    {
      return new TransactionResult(false,
          "An unexpected error occurred: " + e.getMessage());
    }
  }

  public ObservableList<OwnedStockUI> getOwnedStocksUI()
  {
    return ownedStocksUI;
  }

  public DoubleProperty portfolioBalanceProperty()
  {
    return portfolioBalance;
  }

  public DoubleProperty portfolioWorthProperty()
  {
    return portfolioWorth;
  }

  public StringProperty portfolioNameProperty()
  {
    return portfolioName;
  }

  public ObservableList<XYChart.Series<Number, Number>> getWorthSeries()
  {
    return worthSeries;
  }
}