package persistance.fileimplementation;

import entities.Portfolio;
import persistance.interfaces.PortfolioDAO;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class FilePortfolioDAO implements PortfolioDAO
{
  FileUnitOfWork uow;

  public FilePortfolioDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  public void createPortfolio(Portfolio portfolio)
  {
    uow.getPortfolioList().add(portfolio);
  }

  @Override public void updatePortfolio(Portfolio portfolio)
  {
    List<Portfolio> portfolios = uow.getPortfolioList();
    for (int i = 0; i < portfolios.size(); i++)
    {
      if (portfolios.get(i).getId().equals(portfolio.getId()))
      {
        portfolios.set(i, portfolio);
        return;
      }
    }
    Logger.getInstance()
        .log("ERROR", "Portfolio not found: " + portfolio.getId());
    throw new RuntimeException("Portfolio not found: " + portfolio.getId());
  }

  @Override public void deletePortfolio(String id)
  {
    List<Portfolio> portfolios = uow.getPortfolioList();
    boolean removed = portfolios.removeIf(portfolio -> portfolio.getId().equals(id));
    if (!removed)
    {
      Logger.getInstance().log("ERROR", "Portfolio not found: " + id);
      throw new RuntimeException("Portfolio not found: " + id);
    }
  }

  @Override public Portfolio getPortfolioById(String id)
  {
    List<Portfolio> portfolios = uow.getPortfolioList();
    for (Portfolio portfolio : portfolios)
    {
      if (portfolio.getId().equals(id))
      {
        return portfolio;
      }
    }
    Logger.getInstance().log("ERROR", "Portfolio not found: " + id);
    throw new RuntimeException("Portfolio not found: " + id);
  }

  @Override public Portfolio getPortfolioByName(String name)
  {
    List<Portfolio> portfolios = uow.getPortfolioList();
    for (Portfolio portfolio : portfolios)
    {
      if (portfolio.getName().equalsIgnoreCase(name))
      {
        return portfolio;
      }
    }
    Logger.getInstance().log("ERROR", "Portfolio not found: " + name);
    throw new RuntimeException("Portfolio not found: " + name);
  }

  @Override public List<Portfolio> getAllPortfolios()
  {
    List<Portfolio> portfolios = uow.getPortfolioList();
    List<Portfolio> portfoliosCopy = new ArrayList<>();
    for (Portfolio portfolio : portfolios)
    {
      portfoliosCopy.add(
          Portfolio.reloadFromStorage(portfolio.getId(), portfolio.getName(),
              portfolio.getCurrentBalance()));
    }
    return portfoliosCopy;
  }
}
