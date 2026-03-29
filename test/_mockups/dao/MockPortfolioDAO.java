package _mockups.dao;

import entities.Portfolio;
import persistance.fileimplementation.FileUnitOfWork;
import persistance.interfaces.PortfolioDAO;
import shared.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class MockPortfolioDAO implements PortfolioDAO
{
  private List<Portfolio> portfolios = new ArrayList<>();

  public void createPortfolio(Portfolio portfolio)
  {
    portfolios.add(portfolio);
  }

  @Override public void updatePortfolio(Portfolio portfolio)
  {
    for (int i = 0; i < portfolios.size(); i++)
    {
      if (portfolios.get(i).getId().equals(portfolio.getId()))
      {
        portfolios.set(i, portfolio);
        return;
      }
    }
    throw new NotFoundException("Portfolio not found: " + portfolio.getId());
  }

  @Override public void deletePortfolio(String id)
  {
    boolean removed = portfolios.removeIf(
        portfolio -> portfolio.getId().equals(id));
    if (!removed)
    {
      throw new NotFoundException("Portfolio not found: " + id);
    }
  }

  @Override public Portfolio getPortfolioById(String id)
  {
    for (Portfolio portfolio : portfolios)
    {
      if (portfolio.getId().equals(id))
      {
        return portfolio;
      }
    }
    throw new NotFoundException("Portfolio not found: " + id);
  }

  @Override public Portfolio getPortfolioByName(String name)
  {
    for (Portfolio portfolio : portfolios)
    {
      if (portfolio.getName().equalsIgnoreCase(name))
      {
        return portfolio;
      }
    }
    throw new NotFoundException("Portfolio not found: " + name);
  }

  @Override public List<Portfolio> getAllPortfolios()
  {
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
