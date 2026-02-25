package persistance.fileimplementation;

import entities.Transaction;
import entities.TransactionType;
import persistance.interfaces.TransactionDAO;
import shared.logging.Logger;

import java.util.ArrayList;
import java.util.List;

public class FileTransactionDAO implements TransactionDAO
{
  FileUnitOfWork uow;

  public FileTransactionDAO(FileUnitOfWork uow)
  {
    this.uow = uow;
  }

  @Override public void createTransaction(Transaction transaction)
  {
    uow.getTransactionList().add(transaction);
  }

  @Override public void appendTransaction(Transaction transaction)
  {
    uow.getTransactionsAppendOnly().add(transaction);
  }

  @Override public Transaction getTransactionById(String id)
  {
    List<Transaction> transactions = uow.getTransactionList();
    for (Transaction transaction : transactions)
    {
      if (transaction.getId().equals(id))
      {
        return transaction;
      }
    }
    Logger.getInstance().log("ERROR", "Transaction not found: " + id);
    throw new RuntimeException("Transaction not found: " + id);
  }

  @Override public List<Transaction> getTransactionsByPortfolioId(String portfolioId)
  {
    List<Transaction> transactions = uow.getTransactionList();
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

  @Override public List<Transaction> getTransactionsByStockSymbol(String stockSymbol)
  {
    List<Transaction> transactions = uow.getTransactionList();
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
    List<Transaction> transactions = uow.getTransactionList();
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