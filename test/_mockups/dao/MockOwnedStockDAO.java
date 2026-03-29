package _mockups.dao;

import entities.OwnedStock;
import persistance.interfaces.OwnedStockDAO;
import shared.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class MockOwnedStockDAO implements OwnedStockDAO
{
  private List<OwnedStock> ownedStocks = new ArrayList<>();

  @Override public void createOwnedStock(OwnedStock ownedStock)
  {
    ownedStocks.add(ownedStock);
  }

  @Override public void updateOwnedStock(OwnedStock ownedStock)
  {
    for (int i = 0; i < ownedStocks.size(); i++)
    {
      if (ownedStocks.get(i).getId().equals(ownedStock.getId()))
      {
        ownedStocks.set(i, ownedStock);
        return;
      }
    }
    throw new NotFoundException("OwnedStock not found: " + ownedStock.getId());
  }

  @Override public void deleteOwnedStock(String id)
  {
    boolean removed = ownedStocks.removeIf(
        ownedStock -> ownedStock.getId().equals(id));
    if (!removed)
    {
      throw new NotFoundException("OwnedStock not found: " + id);
    }
  }

  @Override public OwnedStock getOwnedStockById(String id)
  {
    for (OwnedStock ownedStock : ownedStocks)
    {
      if (ownedStock.getId().equals(id))
      {
        return ownedStock;
      }
    }
    throw new NotFoundException("OwnedStock not found: " + id);
  }

  @Override public List<OwnedStock> getOwnedStocksByPortfolioId(
      String portfolioId)
  {
    return ownedStocks.stream()
        .filter(stock -> stock.getPortfolioId().equals(portfolioId)).map(
            stock -> OwnedStock.reloadFromStorage(stock.getId(),
                stock.getPortfolioId(), stock.getStockSymbol(),
                stock.getNumberOfShares())).toList();
  }

  @Override public List<OwnedStock> getOwnedStocksBySymbol(String symbol)
  {
    return getAllOwnedStocks().stream()
        .filter(o -> o.getStockSymbol().equals(symbol)).toList();
  }

  @Override public OwnedStock getOwnedStockByPortfolioIdAndSymbol(
      String portfolioId, String symbol)
  {
    return getAllOwnedStocks().stream().filter(
        o -> o.getPortfolioId().equals(portfolioId) && o.getStockSymbol()
            .equals(symbol)).findFirst().orElse(null);
  }

  @Override public List<OwnedStock> getAllOwnedStocks()
  {
    return ownedStocks.stream().map(
        stock -> OwnedStock.reloadFromStorage(stock.getId(),
            stock.getPortfolioId(), stock.getStockSymbol(),
            stock.getNumberOfShares())).toList();
  }

}
