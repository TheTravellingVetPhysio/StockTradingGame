package presentation.controllers;

import business.services.PortfolioService;
import business.services.TransactionService;
import entities.Portfolio;
import presentation.core.PortfolioDependant;
import presentation.viewmodels.TransactionsViewModel;

public class TransactionsController implements PortfolioDependant
{
  private final TransactionsViewModel viewModel;

  public TransactionsController(TransactionService transactionService, PortfolioService portfolioService) {
    this.viewModel = new TransactionsViewModel(transactionService, portfolioService);
  }

  @Override public void setPortfolio(Portfolio portfolio)
  {
    viewModel.loadPortfolio(portfolio);
  }
}
