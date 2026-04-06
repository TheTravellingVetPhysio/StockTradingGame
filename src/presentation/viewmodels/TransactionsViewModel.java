package presentation.viewmodels;

import business.services.PortfolioService;
import business.services.TransactionService;
import entities.Portfolio;

public class TransactionsViewModel
{
  private Portfolio portfolio;
  public TransactionsViewModel(TransactionService transactionService, PortfolioService portfolioService){}

  public void loadPortfolio(Portfolio portfolio) {
    this.portfolio = portfolio;
  }
}
