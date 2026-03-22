import business.observertooling.EventType;
import business.services.*;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import business.stockmarket.simulation.SteadyState;
import entities.Portfolio;
import entities.Stock;
import persistance.fileimplementation.*;
import persistance.interfaces.*;
import shared.configuration.AppConfig;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

import java.util.List;

public class RunApp
{
  public static void main(String[] args) throws InterruptedException
  {
    // Logger
    Logger logger = Logger.getInstance();
    logger.setOutput(new ConsoleLogOutput());
    logger.log("INFO", "Application started");

    // UnitOfWork og DAOs
    FileUnitOfWork uow = new FileUnitOfWork("data");
    StockDAO stockDAO = new FileStockDAO(uow);
    StockPriceHistoryDAO stockPriceHistoryDAO = new FileStockPriceHistoryDAO(
        uow);
    OwnedStockDAO ownedStockDAO = new FileOwnedStockDAO(uow);
    PortfolioDAO portfolioDAO = new FilePortfolioDAO(uow);
    TransactionDAO transactionDAO = new FileTransactionDAO(uow);

    // Services
    StockListenerService stockListenerService = new StockListenerService(
        stockDAO, stockPriceHistoryDAO, uow);
    StockBankruptService stockBankruptService = new StockBankruptService(
        ownedStockDAO, uow);
    StockAlertService stockAlertService = new StockAlertService();
    TransactionService transactionService = new TransactionService(uow, ownedStockDAO, stockDAO, portfolioDAO, transactionDAO, logger);
    PortfolioService portfolioService = new PortfolioService(ownedStockDAO, transactionDAO, portfolioDAO, stockDAO, logger);
    StockMarketService stockMarketService = new StockMarketService(stockDAO, stockPriceHistoryDAO);

    // Listeners
    StockMarket.getInstance()
        .addListener(EventType.STOCK_BANKRUPT, stockAlertService);
    StockMarket.getInstance()
        .addListener(EventType.STOCK_UPDATED, stockListenerService);
    StockMarket.getInstance()
        .addListener(EventType.STOCK_BANKRUPT, stockBankruptService);
    stockListenerService.addListener(EventType.STOCK_UPDATED,
        stockAlertService);
    stockListenerService.addListener(EventType.STOCK_UPDATED,
        arg -> System.out.println("Received update: " + arg));
    stockAlertService.addListener(EventType.STOCK_ALERT,
        arg -> System.out.println("Received stock alert: " + arg));

    // Opret eller load aktier
    List<Stock> existingStocks = stockDAO.getAllStocks();

    if (existingStocks.isEmpty())
    {
      double defaultPrice = AppConfig.getInstance().getDefaultStockPrice();

      uow.beginTransaction();
      stockDAO.createStock(
          new Stock("AAPL", "Apple", defaultPrice, SteadyState.NAME, 1));
      stockDAO.createStock(
          new Stock("GOOGL", "Google", defaultPrice, SteadyState.NAME, 1));
      stockDAO.createStock(
          new Stock("MSFT", "Microsoft", defaultPrice, SteadyState.NAME, 0.9));
      stockDAO.createStock(
          new Stock("VESTA", "Vestas", defaultPrice, SteadyState.NAME, 1.2));
      uow.commit();

      existingStocks = stockDAO.getAllStocks();
    }
    for (Stock stock : existingStocks)
    {
      StockMarket.getInstance().addExistingStock(stock);
    }

    // Opret eller load portfolio
    List<Portfolio> portfolios = portfolioDAO.getAllPortfolios();

    if (portfolios.isEmpty()) {
      uow.beginTransaction();
      portfolioDAO.createPortfolio(Portfolio.createNew("My First Portfolio"));
      uow.commit();
    }

    new MarketTickerThread().start();

    Thread.currentThread().join(); // kør for evigt
  }

}
