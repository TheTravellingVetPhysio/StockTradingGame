package presentation.viewmodels;

import business.services.PortfolioService;
import business.services.StockMarketService;
import entities.Portfolio;

public class DashboardViewModel
{
  private Portfolio portfolio;
  private PortfolioService portfolioService;
  private StockMarketService stockMarketService;

  public DashboardViewModel(PortfolioService portfolioService,
      StockMarketService stockMarketService)
  {
    this.portfolioService = portfolioService;
    this.stockMarketService = stockMarketService;
  }

  public void loadPortfolio(Portfolio portfolio)
  {
    this.portfolio = portfolio;
  }
}
