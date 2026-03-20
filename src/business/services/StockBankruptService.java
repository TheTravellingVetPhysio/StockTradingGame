package business.services;

import business.events.StockBankruptEvent;
import business.observertooling.Listener;
import entities.OwnedStock;
import persistance.interfaces.OwnedStockDAO;
import persistance.interfaces.UnitOfWork;
import shared.exceptions.ServiceLayerException;
import shared.logging.Logger;

import java.util.List;

public class StockBankruptService implements Listener
{
  private final OwnedStockDAO ownedStockDAO;
  private final UnitOfWork unitOfWork;

  public StockBankruptService(OwnedStockDAO ownedStockDAO, UnitOfWork unitOfWork)
  {
    this.ownedStockDAO = ownedStockDAO;
    this.unitOfWork = unitOfWork;
  }

  @Override
  public void update(Object arg)
  {
    if (arg instanceof StockBankruptEvent event)
    {
      handleBankruptcy(event);
    }
  }

  private void handleBankruptcy(StockBankruptEvent event)
  {
    try
    {
      unitOfWork.beginTransaction();

      List<OwnedStock> ownedStocks = ownedStockDAO.getOwnedStocksBySymbol(event.symbol());

      if (ownedStocks.isEmpty())
      {
        Logger.getInstance().log("INFO", "No owned stocks found for bankrupt stock: "
            + event.symbol());
        unitOfWork.rollback();
        return;
      }

      for (OwnedStock ownedStock : ownedStocks)
      {
        ownedStock.setNumberOfShares(0);
        ownedStockDAO.updateOwnedStock(ownedStock);
      }

      unitOfWork.commit();

      Logger.getInstance().log("WARN", "Bankruptcy processed for: "
          + event.symbol() + " | Affected portfolios: " + ownedStocks.size());
    }
    catch (ServiceLayerException e)
    {
      unitOfWork.rollback();
      Logger.getInstance().log("ERROR", "Failed to process bankruptcy: " + e.getMessage());
      throw e;
    }
    catch (Exception e)
    {
      unitOfWork.rollback();
      Logger.getInstance().log("ERROR", "Unexpected error: " + e.getMessage());
      throw new ServiceLayerException("Failed to process bankruptcy", e);
    }
  }
}