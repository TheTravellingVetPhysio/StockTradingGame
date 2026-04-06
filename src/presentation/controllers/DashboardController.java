package presentation.controllers;

import business.services.PortfolioService;
import business.services.StockMarketService;
import entities.Portfolio;
import presentation.core.PortfolioDependant;
import presentation.viewmodels.DashboardViewModel;
import presentation.viewmodels.TransactionsViewModel;

public class DashboardController implements PortfolioDependant
{
  private PortfolioService portfolioService;
  private StockMarketService stockMarketService;
  private final DashboardViewModel viewModel;

  public DashboardController(PortfolioService portfolioService, StockMarketService stockMarketService) {
    this.viewModel = new DashboardViewModel(portfolioService, stockMarketService);
  }

  @Override public void setPortfolio(Portfolio portfolio)
  {
    viewModel.loadPortfolio(portfolio);
  }
}
