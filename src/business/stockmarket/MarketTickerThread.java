package business.stockmarket;

import shared.configuration.AppConfig;
import shared.logging.Logger;

public class MarketTickerThread
{
  private final StockMarket stockMarket = StockMarket.getInstance();
  private final int updateFrequency = AppConfig.getInstance()
      .getUpdateFrequencyInMs();
  Thread thread;

  public void start()
  {
    thread = new Thread(() -> {
      Logger.getInstance().log("INFO", "Market ticker started");
      while (true)
      {
        try
        {
          stockMarket.updateAllStocks();
          Thread.sleep(updateFrequency);
        }
        catch (InterruptedException e)
        {
          Logger.getInstance().log("ERROR", "Market ticker interrupted");
          Thread.currentThread().interrupt();
          break;
        }
      }
    });
    thread.setDaemon(true);
    thread.start();
  }

  public void stop()
  {
    if (thread != null)
    {
      thread.interrupt();
    }
  }
}