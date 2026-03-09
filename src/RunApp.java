import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

public class RunApp
{
  public static void main(String[] args) throws InterruptedException
  {
    Logger logger = Logger.getInstance();
    logger.setOutput(new ConsoleLogOutput());

    logger.log("INFO", "Application started");

    StockMarket.getInstance().addNewStock("AAPL");
    StockMarket.getInstance().addNewStock("GOOGL");
    StockMarket.getInstance().addNewStock("MSFT");
    StockMarket.getInstance().addNewStock("VESTA");

    new MarketTickerThread().start();

    Thread.currentThread().join(); // kør for evigt
  }
}
