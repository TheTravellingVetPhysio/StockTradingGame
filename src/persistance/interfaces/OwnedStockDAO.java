package persistance.interfaces;

import entities.OwnedStock;
import java.util.List;

public interface OwnedStockDAO
{
  void createOwnedStock(OwnedStock ownedStock);
  void updateOwnedStock(OwnedStock ownedStock);
  void deleteOwnedStock(String id);
  OwnedStock getOwnedStockById(String id);
  List<OwnedStock> getOwnedStocksByPortfolioId(String portfolioId);
  List<OwnedStock> getAllOwnedStocks();
}