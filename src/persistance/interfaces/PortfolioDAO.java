package persistance.interfaces;

import entities.Portfolio;

import java.util.List;

public interface PortfolioDAO
{
  void createPortfolio(Portfolio portfolio);
  void updatePortfolio(Portfolio portfolio);
  void deletePortfolio(String id);
  Portfolio getPortfolioById(String id);
  Portfolio getPortfolioByName(String name);
  List<Portfolio> getAllPortfolios();
}
