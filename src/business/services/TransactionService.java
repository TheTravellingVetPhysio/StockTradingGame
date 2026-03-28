package business.services;

import business.stockmarket.simulation.BankruptState;
import dto.StockBuyRequest;
import dto.StockSellRequest;
import entities.*;
import persistance.interfaces.*;
import shared.configuration.AppConfig;
import shared.exceptions.InsufficientFundsException;
import shared.exceptions.PortfolioNotFoundException;
import shared.exceptions.StockBankruptException;
import shared.exceptions.StockNotFoundException;
import shared.logging.Logger;

import java.time.LocalDateTime;

public class TransactionService
{
  private final UnitOfWork unitOfWork;
  private final OwnedStockDAO ownedStockDAO;
  private final StockDAO stockDAO;
  private final PortfolioDAO portfolioDAO;
  private final TransactionDAO transactionDAO;
  private final Logger logger;

  public TransactionService(UnitOfWork unitOfWork, OwnedStockDAO ownedStockDAO,
      StockDAO stockDAO, PortfolioDAO portfolioDAO,
      TransactionDAO transactionDAO, Logger logger)
  {
    this.unitOfWork = unitOfWork;
    this.ownedStockDAO = ownedStockDAO;
    this.stockDAO = stockDAO;
    this.portfolioDAO = portfolioDAO;
    this.transactionDAO = transactionDAO;
    this.logger = logger;
  }

  public void buyStock(StockBuyRequest request)
  {
    unitOfWork.beginTransaction();

    try
    {
      ensureQuantityMoreThanZero(request.quantity());

      Stock stock = stockDAO.getStockBySymbol(request.stockSymbol());
      ensureStockIsNotBankrupt(stock, request.stockSymbol());

      double pricePerShare = stock.getCurrentPrice();
      double fee = AppConfig.getInstance().getTransactionFee();

      Portfolio portfolio = portfolioDAO.getPortfolioById(
          request.portfolioId());
      ensureBalanceEqualOrLargerThanTotalCost(portfolio, request.quantity(),
          pricePerShare, fee);

      OwnedStock ownedStock = ownedStockDAO.getOwnedStockByPortfolioIdAndSymbol(
          request.portfolioId(), request.stockSymbol());

      if (ownedStock != null)
      {
        int currentNumberOfShares = ownedStock.getNumberOfShares();
        int updatedNumberOfShares = currentNumberOfShares + request.quantity();

        ownedStock.setNumberOfShares(updatedNumberOfShares);
        ownedStockDAO.updateOwnedStock(ownedStock);
      }

      else
      {
        ownedStock = OwnedStock.createNew(request.portfolioId(),
            request.stockSymbol(), request.quantity());
        ownedStockDAO.createOwnedStock(ownedStock);
      }

      double currentBalance = portfolio.getCurrentBalance();
      double completeAmount = pricePerShare * request.quantity() + fee;

      portfolio.setCurrentBalance(currentBalance - completeAmount);
      portfolioDAO.updatePortfolio(portfolio);

      Transaction transaction = Transaction.createNew(request.portfolioId(),
          request.stockSymbol(), TransactionType.BUY, request.quantity(),
          pricePerShare, fee, LocalDateTime.now());
      transactionDAO.createTransaction(transaction);

      unitOfWork.commit();
      logger.log("INFO",
          "Purchase successful: " + request.quantity() + " shares of "
              + request.stockSymbol());
    }

    catch (Exception e)
    {
      unitOfWork.rollback();
      logger.log("ERROR", "Purchase could not go through: " + e.getMessage());
      throw e;
    }
  }

  public void sellStock(StockSellRequest request)
  {
    unitOfWork.beginTransaction();

    try
    {
      ensureQuantityMoreThanZero(request.quantity());

      Stock stock = stockDAO.getStockBySymbol(request.stockSymbol());
      ensureStockIsNotBankrupt(stock, request.stockSymbol());

      OwnedStock ownedStock = ownedStockDAO.getOwnedStockByPortfolioIdAndSymbol(
          request.portfolioId(), request.stockSymbol());
      checkIfStockIsOwned(ownedStock);
      ensureEnoughSharesOfOwnedStockToSell(request.quantity(), ownedStock);

      Portfolio portfolio = portfolioDAO.getPortfolioById(
          request.portfolioId());

      int currentNumberOfShares = ownedStock.getNumberOfShares();
      int updatedNumberOfShares = currentNumberOfShares - request.quantity();

      if (updatedNumberOfShares == 0)
      {
        ownedStockDAO.deleteOwnedStock(ownedStock.getId());
      }
      else
      {
        ownedStock.setNumberOfShares(updatedNumberOfShares);
        ownedStockDAO.updateOwnedStock(ownedStock);
      }

      double pricePerShare = stock.getCurrentPrice();
      double fee = AppConfig.getInstance().getTransactionFee();
      double currentBalance = portfolio.getCurrentBalance();
      double completeAmount = pricePerShare * request.quantity() - fee;

      portfolio.setCurrentBalance(currentBalance + completeAmount);
      portfolioDAO.updatePortfolio(portfolio);

      Transaction transaction = Transaction.createNew(request.portfolioId(),
          request.stockSymbol(), TransactionType.SELL, request.quantity(),
          pricePerShare, fee, LocalDateTime.now());
      transactionDAO.createTransaction(transaction);

      unitOfWork.commit();
      logger.log("INFO",
          "Sale successful: " + request.quantity() + " shares of "
              + request.stockSymbol());
    }

    catch (Exception e)
    {
      unitOfWork.rollback();
      logger.log("ERROR", "Sale could not go through: " + e.getMessage());
      throw e;
    }
  }

  private void ensureQuantityMoreThanZero(int quantity)
  {
    if (quantity
        < 1)        // Ingen upper limit, bare for at undgå en masse 0-transaktioner
    {
      throw new IllegalArgumentException("Quantity must be at least 1");
    }
  }

  private void checkIfStockIsOwned(OwnedStock ownedStock)
  {
    if (ownedStock == null)
    {
      throw new IllegalArgumentException("Stock is not owned.");
    }
  }

  private void ensureEnoughSharesOfOwnedStockToSell(int quantity,
      OwnedStock ownedStock)
  {
    if (quantity > ownedStock.getNumberOfShares())
    {
      throw new IllegalArgumentException(
          "Too many shares entered compared to owned in portfolio.");
    }
  }

  private void ensureStockIsNotBankrupt(Stock stock, String stockSymbol)
  {
    if (stock.getCurrentState().equals(BankruptState.NAME))
    {
      throw new StockBankruptException(stock.getSymbol());
    }
  }

  private void ensureBalanceEqualOrLargerThanTotalCost(Portfolio portfolio,
      int quantity, double pricePerShare, double fee)
  {
    if (portfolio.getCurrentBalance() < (pricePerShare * quantity + fee))
    {
      throw new InsufficientFundsException(portfolio.getId());
    }
  }

}