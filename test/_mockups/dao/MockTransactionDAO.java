package _mockups.dao;

import entities.Transaction;
import persistance.interfaces.TransactionDAO;
import shared.exceptions.NotFoundException;

import java.util.ArrayList;
import java.util.List;

public class MockTransactionDAO implements TransactionDAO
{
  private List<Transaction> transactions = new ArrayList<>();

  @Override public void createTransaction(Transaction transaction)
  {
    transactions.add(transaction);
  }

  @Override public void appendTransaction(Transaction transaction)
  {
    transactions.add(transaction);
  }

  @Override public Transaction getTransactionById(String id)
  {
    for (Transaction transaction : transactions)
    {
      if (transaction.getId().equals(id))
      {
        return transaction;
      }
    }
    throw new NotFoundException("Transaction not found: " + id);
  }

  @Override public List<Transaction> getTransactionsByPortfolioId(
      String portfolioId)
  {
    List<Transaction> result = new ArrayList<>();
    for (Transaction transaction : transactions)
    {
      if (transaction.getPortfolioId().equals(portfolioId))
      {
        result.add(Transaction.reloadFromStorage(transaction.getId(),
            transaction.getPortfolioId(), transaction.getStockSymbol(),
            transaction.getType(), transaction.getQuantity(),
            transaction.getPricePerShare(), transaction.getTotalAmount(),
            transaction.getFee(), transaction.getTimestamp()));
      }
    }
    return result;
  }

  @Override public List<Transaction> getTransactionsByStockSymbol(
      String stockSymbol)
  {
    List<Transaction> result = new ArrayList<>();
    for (Transaction transaction : transactions)
    {
      if (transaction.getStockSymbol().equals(stockSymbol))
      {
        result.add(Transaction.reloadFromStorage(transaction.getId(),
            transaction.getPortfolioId(), transaction.getStockSymbol(),
            transaction.getType(), transaction.getQuantity(),
            transaction.getPricePerShare(), transaction.getTotalAmount(),
            transaction.getFee(), transaction.getTimestamp()));
      }
    }
    return result;
  }

  @Override public List<Transaction> getAllTransactions()
  {
    List<Transaction> transactionsCopy = new ArrayList<>();
    for (Transaction transaction : transactions)
    {
      transactionsCopy.add(Transaction.reloadFromStorage(transaction.getId(),
          transaction.getPortfolioId(), transaction.getStockSymbol(),
          transaction.getType(), transaction.getQuantity(),
          transaction.getPricePerShare(), transaction.getTotalAmount(),
          transaction.getFee(), transaction.getTimestamp()));
    }
    return transactionsCopy;
  }

}
