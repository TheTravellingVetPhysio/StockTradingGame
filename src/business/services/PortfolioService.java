package business.services;

import entities.OwnedStock;
import entities.Portfolio;
import entities.Stock;
import entities.Transaction;
import persistance.interfaces.OwnedStockDAO;
import persistance.interfaces.PortfolioDAO;
import persistance.interfaces.StockDAO;
import persistance.interfaces.TransactionDAO;
import shared.logging.Logger;

import java.util.List;

public class PortfolioService
{
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final PortfolioDAO portfolioDAO;
  private final StockDAO stockDAO;

  public PortfolioService(OwnedStockDAO ownedStockDAO,
      TransactionDAO transactionDAO, PortfolioDAO portfolioDAO,
      StockDAO stockDAO)
  {
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
    this.portfolioDAO = portfolioDAO;
    this.stockDAO = stockDAO;
  }

  public List<OwnedStock> getOwnedStocks(String portfolioId)
  {
    return ownedStockDAO.getOwnedStocksByPortfolioId(portfolioId);
  }

  public List<Transaction> getTransactionHistory(String portfolioId, int page,
      int pagesize)
  {
    List<Transaction> allTransactions = transactionDAO.getTransactionsByPortfolioId(
        portfolioId);
    int from = page * pagesize;
    int to = Math.min(from + pagesize, allTransactions.size());

    return allTransactions.subList(from, to);
  }

  public double getPortfolioBalance(String portfolioId)
  {
    Portfolio portfolio = portfolioDAO.getPortfolioById(portfolioId);
    return portfolio.getCurrentBalance();
  }

  public double getPortfolioWorth(String portfolioId)
  {
    double currentBalance = getPortfolioBalance(portfolioId);
    double stockValue = 0;
    List<OwnedStock> ownedStocks = getOwnedStocks(portfolioId);

    for (OwnedStock ownedStock : ownedStocks)
    {
      Stock stock = stockDAO.getStockBySymbol(ownedStock.getStockSymbol());
      stockValue += ownedStock.getNumberOfShares() * stock.getCurrentPrice();
    }

    return currentBalance + stockValue;
  }

}
