package business.services;

import business.observertooling.EventType;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import business.stockmarket.simulation.SteadyState;
import entities.Portfolio;
import entities.Stock;
import presentation.core.ApplicationContext;
import shared.configuration.AppConfig;

import java.util.List;

public class GameService
{

  private final ApplicationContext applicationContext;

  public GameService(ApplicationContext applicationContext)
  {
    this.applicationContext = applicationContext;
  }

  public void initialize()
  {
    setupListeners();
    loadOrCreateStocks();
    loadOrCreatePortfolio();
    new MarketTickerThread().start();
  }

  private void setupListeners()
  {
    StockMarket.getInstance()
        .addListener(EventType.STOCK_BANKRUPT, applicationContext.getStockAlertService());
    StockMarket.getInstance()
        .addListener(EventType.STOCK_UPDATED, applicationContext.getStockListenerService());
    StockMarket.getInstance()
        .addListener(EventType.STOCK_BANKRUPT, applicationContext.getStockBankruptService());
    applicationContext.getStockListenerService()
        .addListener(EventType.STOCK_UPDATED, applicationContext.getStockAlertService());
    applicationContext.getStockListenerService().addListener(EventType.STOCK_UPDATED,
        arg -> System.out.println("Received update: " + arg));
    applicationContext.getStockAlertService().addListener(EventType.STOCK_ALERT,
        arg -> System.out.println("Received stock alert: " + arg));
  }

  private void loadOrCreateStocks()
  {
    List<Stock> existingStocks = applicationContext.getStockDAO().getAllStocks();

    if (existingStocks.isEmpty())
    {
      double defaultPrice = AppConfig.getInstance().getDefaultStockPrice();
      applicationContext.getUow().beginTransaction();
      applicationContext.getStockDAO().createStock(
          new Stock("AAPL", "Apple", defaultPrice, SteadyState.NAME, 1.0));
      applicationContext.getStockDAO().createStock(
          new Stock("GOOGL", "Google", defaultPrice, SteadyState.NAME, 1.0));
      applicationContext.getStockDAO().createStock(
          new Stock("MSFT", "Microsoft", defaultPrice, SteadyState.NAME, 0.9));
      applicationContext.getStockDAO().createStock(
          new Stock("VESTA", "Vestas", defaultPrice, SteadyState.NAME, 1.2));
      applicationContext.getStockDAO().createStock(
          new Stock("AMZN", "Amazon", defaultPrice, SteadyState.NAME, 1.1));
      applicationContext.getStockDAO().createStock(
          new Stock("TSLA", "Tesla", defaultPrice, SteadyState.NAME, 1.5));
      applicationContext.getStockDAO().createStock(
          new Stock("NOVO", "Novo Nordisk", defaultPrice, SteadyState.NAME,
              1.0));
      applicationContext.getStockDAO().createStock(
          new Stock("NVDA", "Nvidia", defaultPrice, SteadyState.NAME, 1.3));
      applicationContext.getStockDAO().createStock(
          new Stock("MAERSK", "Mærsk", defaultPrice, SteadyState.NAME, 0.8));
      applicationContext.getStockDAO().createStock(
          new Stock("META", "Meta", defaultPrice, SteadyState.NAME, 1.1));
      applicationContext.getUow().commit();
      existingStocks = applicationContext.getStockDAO().getAllStocks();
    }

    for (Stock stock : existingStocks)
    {
      StockMarket.getInstance().addExistingStock(stock);
    }
  }

  private void loadOrCreatePortfolio()
  {
    List<Portfolio> portfolios = applicationContext.getPortfolioDAO().getAllPortfolios();
    if (portfolios.isEmpty())
    {
      applicationContext.getUow().beginTransaction();
      applicationContext.getPortfolioDAO()
          .createPortfolio(Portfolio.createNew("My First Portfolio"));
      applicationContext.getUow().commit();
    }
  }
}