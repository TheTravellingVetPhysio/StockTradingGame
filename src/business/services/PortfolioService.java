package business.services;

import entities.OwnedStock;
import entities.Portfolio;
import entities.Stock;
import entities.Transaction;
import persistance.interfaces.OwnedStockDAO;
import persistance.interfaces.PortfolioDAO;
import persistance.interfaces.StockDAO;
import persistance.interfaces.TransactionDAO;
import shared.exceptions.PortfolioNotFoundException;
import shared.exceptions.StockNotFoundException;
import shared.logging.Logger;

import java.util.List;

public class PortfolioService
{
  private final OwnedStockDAO ownedStockDAO;
  private final TransactionDAO transactionDAO;
  private final PortfolioDAO portfolioDAO;
  private final StockDAO stockDAO;
  private final Logger logger;

  public PortfolioService(OwnedStockDAO ownedStockDAO,
      TransactionDAO transactionDAO, PortfolioDAO portfolioDAO,
      StockDAO stockDAO, Logger logger)
  {
    this.ownedStockDAO = ownedStockDAO;
    this.transactionDAO = transactionDAO;
    this.portfolioDAO = portfolioDAO;
    this.stockDAO = stockDAO;
    this.logger = logger;
  }

  public List<OwnedStock> getOwnedStocks(String portfolioId)
  {
    return ownedStockDAO.getOwnedStocksByPortfolioId(portfolioId);
  }

  public List<Transaction> getTransactionHistory(String portfolioId)
  {
    return transactionDAO.getTransactionsByPortfolioId(portfolioId);
  }

  public double getPortfolioBalance(String portfolioId)
  {
    Portfolio portfolio = portfolioDAO.getPortfolioById(portfolioId);

    if (portfolio == null)
    {
      PortfolioNotFoundException e = new PortfolioNotFoundException(portfolioId);
      logger.log("ERROR", e.getMessage());
      throw e;
    }

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
      if (stock == null) {
        StockNotFoundException e = new StockNotFoundException(ownedStock.getStockSymbol());
        logger.log("ERROR", e.getMessage());
        throw e;
      }

      stockValue += ownedStock.getNumberOfShares() * stock.getCurrentPrice();
    }

    return currentBalance + stockValue;
  }

}
