import business.observertooling.EventType;
import business.services.StockBankruptService;
import business.services.StockListenerService;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import business.stockmarket.simulation.SteadyState;
import entities.Stock;
import persistance.fileimplementation.FileOwnedStockDAO;
import persistance.fileimplementation.FileStockDAO;
import persistance.fileimplementation.FileStockPriceHistoryDAO;
import persistance.fileimplementation.FileUnitOfWork;
import persistance.interfaces.OwnedStockDAO;
import persistance.interfaces.StockDAO;
import persistance.interfaces.StockPriceHistoryDAO;
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
    StockPriceHistoryDAO stockPriceHistoryDAO = new FileStockPriceHistoryDAO(uow);
    OwnedStockDAO ownedStockDAO = new FileOwnedStockDAO(uow);

    // Services
    StockListenerService stockListenerService = new StockListenerService(stockDAO, stockPriceHistoryDAO, uow);
    StockBankruptService stockBankruptService = new StockBankruptService(ownedStockDAO, uow);

    // Registrer listeners på StockMarket
    StockMarket.getInstance().addListener(EventType.STOCK_UPDATED, stockListenerService);
    StockMarket.getInstance().addListener(EventType.STOCK_BANKRUPT, stockBankruptService);

    // Opret eller load aktier
    List<Stock> existingStocks = stockDAO.getAllStocks();

    if (existingStocks.isEmpty()) {
      double defaultPrice = AppConfig.getInstance().getDefaultStockPrice();

      uow.beginTransaction();
      stockDAO.createStock(new Stock("AAPL", "Apple", defaultPrice, SteadyState.NAME));
      stockDAO.createStock(new Stock("GOOGL", "Google", defaultPrice, SteadyState.NAME));
      stockDAO.createStock(new Stock("MSFT", "Microsoft", defaultPrice, SteadyState.NAME));
      stockDAO.createStock(new Stock("VESTA", "Vestas", defaultPrice, SteadyState.NAME));
      uow.commit();

      StockMarket.getInstance().addNewStock("AAPL", "Apple");
      StockMarket.getInstance().addNewStock("GOOGL", "Google");
      StockMarket.getInstance().addNewStock("MSFT", "Microsoft");
      StockMarket.getInstance().addNewStock("VESTA", "Vestas");
    } else {
      for (Stock stock : existingStocks) {
        StockMarket.getInstance().addExistingStock(stock);
      }
    }


    new MarketTickerThread().start();

    Thread.currentThread().join(); // kør for evigt
  }
}
