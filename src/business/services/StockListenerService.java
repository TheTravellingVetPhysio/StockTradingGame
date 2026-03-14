package business.services;

import business.events.StockUpdateEvent;
import business.observertooling.Listener;
import entities.Stock;
import entities.StockPriceHistory;
import persistance.interfaces.StockDAO;
import persistance.interfaces.StockPriceHistoryDAO;
import persistance.interfaces.UnitOfWork;
import shared.logging.Logger;

import java.time.LocalDateTime;

public class StockListenerService implements Listener
{
    private final StockDAO stockDAO;
    private final StockPriceHistoryDAO stockPriceHistoryDAO;
    private final UnitOfWork unitOfWork;

  public StockListenerService(StockDAO stockDAO,
      StockPriceHistoryDAO stockPriceHistoryDAO, UnitOfWork unitOfWork)
  {
    this.stockDAO = stockDAO;
    this.stockPriceHistoryDAO = stockPriceHistoryDAO;
    this.unitOfWork = unitOfWork;
  }

  @Override public void update(Object arg)
  {
    if (arg instanceof StockUpdateEvent event)
    {
      handleStockUpdate(event);
    }
  }

  private void handleStockUpdate(StockUpdateEvent event)
  {
    try
    {
      unitOfWork.beginTransaction();

      Stock stock = stockDAO.getStockBySymbol(event.symbol());

      if (stock == null)
      {
        Logger.getInstance().log("WARN", "Stock not found: " + event.symbol());
        unitOfWork.rollback();
        return;
      }

      stock.setCurrentPrice(event.price());
      stock.setCurrentState(event.state());
      stockDAO.updateStock(stock);

      StockPriceHistory history = StockPriceHistory.createNew(
          event.symbol(),
          event.price(),
          LocalDateTime.now()
      );
      stockPriceHistoryDAO.appendStockPriceHistory(history);

      unitOfWork.commit();

      Logger.getInstance().log("INFO", "Stock updated: " + event.symbol()
          + " | Price: " + event.price()
          + " | State: " + event.state());
    }
    catch (Exception e)
    {
      unitOfWork.rollback();
      Logger.getInstance().log("ERROR", "Failed to update stock: " + e.getMessage());
    }
  }
}
