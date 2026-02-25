package persistance.interfaces;

import entities.Transaction;
import java.util.List;

public interface TransactionDAO
{
  void createTransaction(Transaction transaction);
  void appendTransaction(Transaction transaction);
  Transaction getTransactionById(String id);
  List<Transaction> getTransactionsByPortfolioId(String portfolioId);
  List<Transaction> getTransactionsByStockSymbol(String stockSymbol);
  List<Transaction> getAllTransactions();
}