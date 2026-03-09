package persistance.fileimplementation;

import entities.OwnedStock;
import persistance.interfaces.OwnedStockDAO;
import shared.logging.Logger;

import java.util.List;

public class FileOwnedStockDAO implements OwnedStockDAO
{
  FileUnitOfWork uow;

  public FileOwnedStockDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void createOwnedStock(OwnedStock ownedStock)
  {
    uow.getOwnedStockList().add(ownedStock);
  }

  @Override public void updateOwnedStock(OwnedStock ownedStock)
  {
    List<OwnedStock> ownedStocks = uow.getOwnedStockList();
    for (int i = 0; i < ownedStocks.size(); i++)
    {
      if (ownedStocks.get(i).getId().equals(ownedStock.getId()))
      {
        ownedStocks.set(i, ownedStock);
        return;
      }
    }
    Logger.getInstance().log("ERROR", "OwnedStock not found: " + ownedStock.getId());
    throw new RuntimeException("OwnedStock not found: " + ownedStock.getId());
  }

  @Override public void deleteOwnedStock(String id)
  {
    List<OwnedStock> ownedStocks = uow.getOwnedStockList();
    boolean removed = ownedStocks.removeIf(ownedStock -> ownedStock.getId().equals(id));
    if (!removed)
    {
      Logger.getInstance().log("ERROR", "OwnedStock not found: " + id);
      throw new RuntimeException("OwnedStock not found: " + id);
    }
  }

  @Override public OwnedStock getOwnedStockById(String id)
  {
    List<OwnedStock> ownedStocks = uow.getOwnedStockList();
    for (OwnedStock ownedStock : ownedStocks)
    {
      if (ownedStock.getId().equals(id))
      {
        return ownedStock;
      }
    }
    Logger.getInstance().log("ERROR", "OwnedStock not found: " + id);
    throw new RuntimeException("OwnedStock not found: " + id);
  }

  @Override
  public List<OwnedStock> getOwnedStocksByPortfolioId(String portfolioId) {
    return uow.getOwnedStockList()
        .stream()
        .filter(stock -> stock.getPortfolioId().equals(portfolioId))
        .map(stock -> OwnedStock.reloadFromStorage(
            stock.getId(),
            stock.getPortfolioId(),
            stock.getStockSymbol(),
            stock.getNumberOfShares()
        ))
        .toList();
  }

  @Override
  public List<OwnedStock> getAllOwnedStocks() {
    return uow.getOwnedStockList()
        .stream()
        .map(stock -> OwnedStock.reloadFromStorage(
            stock.getId(),
            stock.getPortfolioId(),
            stock.getStockSymbol(),
            stock.getNumberOfShares()
        ))
        .toList();
  }
}