package presentation.core;

import business.services.*;
import persistance.fileimplementation.*;
import persistance.interfaces.*;
import presentation.controllers.DashboardController;
import presentation.controllers.MainViewController;
import presentation.controllers.StockMarketController;
import presentation.controllers.TransactionsController;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

public class ApplicationContext
{

  // DAOs
  private final FileUnitOfWork uow;
  private final StockDAO stockDAO;
  private final StockPriceHistoryDAO stockPriceHistoryDAO;
  private final OwnedStockDAO ownedStockDAO;
  private final PortfolioDAO portfolioDAO;
  private final TransactionDAO transactionDAO;

  // Services
  private final StockListenerService stockListenerService;
  private final StockBankruptService stockBankruptService;
  private final StockAlertService stockAlertService;
  private final TransactionService transactionService;
  private final PortfolioService portfolioService;
  private final StockMarketService stockMarketService;

  // ApplicationContext instantiering + ViewManager
  private static ApplicationContext instance;
  private ViewManager viewManager;

  public static ApplicationContext getInstance() {
    if (instance == null) {
      instance = new ApplicationContext();
    }
    return instance;
  }

  private ApplicationContext()
  {
    Logger logger = Logger.getInstance();
    logger.setOutput(new ConsoleLogOutput());
    logger.log("INFO", "Application started");

    uow = new FileUnitOfWork("data");
    stockDAO = new FileStockDAO(uow);
    stockPriceHistoryDAO = new FileStockPriceHistoryDAO(uow);
    ownedStockDAO = new FileOwnedStockDAO(uow);
    portfolioDAO = new FilePortfolioDAO(uow);
    transactionDAO = new FileTransactionDAO(uow);

    stockListenerService = new StockListenerService(stockDAO,
        stockPriceHistoryDAO, uow);
    stockBankruptService = new StockBankruptService(ownedStockDAO, uow);
    stockAlertService = new StockAlertService();
    transactionService = new TransactionService(uow, ownedStockDAO, stockDAO,
        portfolioDAO, transactionDAO, logger);
    portfolioService = new PortfolioService(ownedStockDAO, transactionDAO,
        portfolioDAO, stockDAO);
    stockMarketService = new StockMarketService(stockDAO, stockPriceHistoryDAO);
  }

  public void setViewManager(ViewManager viewManager)
  {
    this.viewManager = viewManager;
  }

  public Object getController(Class<?> controllerClass)
  {
      if (controllerClass == MainViewController.class)
        return new MainViewController(viewManager, portfolioService);

      if (controllerClass == DashboardController.class)
        return new DashboardController(portfolioService, stockMarketService);

      if (controllerClass == TransactionsController.class)
        return new TransactionsController(transactionService, portfolioService);

      if (controllerClass == StockMarketController.class)
        return new StockMarketController(stockMarketService, transactionService, stockListenerService);

      throw new IllegalArgumentException("Unknown controller: " + controllerClass.getName());
  }

  // Getters
  public FileUnitOfWork getUow()
  {
    return uow;
  }

  public StockDAO getStockDAO()
  {
    return stockDAO;
  }

  public PortfolioDAO getPortfolioDAO()
  {
    return portfolioDAO;
  }

  public StockListenerService getStockListenerService()
  {
    return stockListenerService;
  }

  public StockBankruptService getStockBankruptService()
  {
    return stockBankruptService;
  }

  public StockAlertService getStockAlertService()
  {
    return stockAlertService;
  }

  public TransactionService getTransactionService()
  {
    return transactionService;
  }

  public PortfolioService getPortfolioService()
  {
    return portfolioService;
  }

  public StockMarketService getStockMarketService()
  {
    return stockMarketService;
  }

}

