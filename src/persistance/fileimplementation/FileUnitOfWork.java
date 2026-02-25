package persistance.fileimplementation;

import entities.*;
import persistance.interfaces.UnitOfWork;
import shared.logging.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FileUnitOfWork implements UnitOfWork
{
  private String directoryPath;
  private List<Portfolio> portfolioList;
  private List<OwnedStock> ownedStockList;
  private List<Stock> stockList;
  private List<StockPriceHistory> stockPriceHistoryList;
  private List<Transaction> transactionList;
  private List<StockPriceHistory> stockPriceHistoryBuffer = new ArrayList<>();
  private List<Transaction> transactionBuffer = new ArrayList<>();

  private static final Object FILE_WRITE_LOCK = new Object();

  // CONSTRUCTOR + TJEK/OPRETTELSE AF FILER

  public FileUnitOfWork(String directoryPath)
  {
    this.directoryPath = directoryPath;
    ensureFileExists();
  }

  private void ensureFileExists()
  {
    createFileIfNotExists(directoryPath + "/portfoliolist.txt");
    createFileIfNotExists(directoryPath + "/ownedstocklist.txt");
    createFileIfNotExists(directoryPath + "/stocklist.txt");
    createFileIfNotExists(directoryPath + "/stockpricehistorylist.txt");
    createFileIfNotExists(directoryPath + "/transactionlist.txt");
  }

  private void createFileIfNotExists(String filePath)
  {
    File file = new File(filePath);
    if (!file.exists())
    {
      try
      {
        file.createNewFile();
      }
      catch (IOException e)
      {
        Logger.getInstance().log("ERROR",
            "Failed to create file: " + filePath + "-" + e.getMessage());
        throw new RuntimeException("Failed to create file: " + filePath, e);
      }
    }
  }

  // PORTFOLIO SPECIFIC METHODS

  public List<Portfolio> getPortfolioList()
  {
    if (portfolioList == null)
    {
      portfolioList = new ArrayList<>();
      for (String line : readAllLines(directoryPath + "/portfoliolist.txt"))
      {
        portfolioList.add(portfolioFromPSV(line));
      }
    }
    return portfolioList;
  }

  private Portfolio portfolioFromPSV(String line)
  {
    String[] parts = splitPSV(line);
    return Portfolio.reloadFromStorage(parts[0], parts[1],
        Double.parseDouble(parts[2]));
  }

  private String toPSV(Portfolio portfolio)
  {
    return portfolio.getId() + "¤" + portfolio.getName() + "¤"
        + portfolio.getCurrentBalance();
  }

  // OWNEDSTOCK SPECIFIC METHODS

  public List<OwnedStock> getOwnedStockList()
  {
    if (ownedStockList == null)
    {
      ownedStockList = new ArrayList<>();
      for (String line : readAllLines(directoryPath + "/ownedstocklist.txt"))
      {
        ownedStockList.add(ownedStockFromPSV(line));
      }
    }
    return ownedStockList;
  }

  private OwnedStock ownedStockFromPSV(String line)
  {
    String[] parts = splitPSV(line);
    return OwnedStock.reloadFromStorage(parts[0], parts[1], parts[2],
        Integer.parseInt(parts[3]));
  }

  private String toPSV(OwnedStock ownedStock)
  {
    return ownedStock.getId() + "¤" + ownedStock.getPortfolioId() + "¤"
        + ownedStock.getStockSymbol() + "¤" + ownedStock.getNumberOfShares();
  }

  // TRANSITION SPECIFIC METHODS

  public List<Transaction> getTransactionList()
  {
    if (transactionList == null)
    {
      transactionList = new ArrayList<>();
      for (String line : readAllLines(directoryPath + "/transactionlist.txt"))
      {
        transactionList.add(transactionFromPSV(line));
      }
    }
    return transactionList;
  }

  private Transaction transactionFromPSV(String line)
  {
    String[] parts = splitPSV(line);
    String id = parts[0];
    String portfolioId = parts[1];
    String stockSymbol = parts[2];
    TransactionType type = TransactionType.valueOf(parts[3]);
    int quantity = Integer.parseInt(parts[4]);
    double pricePerShare = Double.parseDouble(parts[5]);
    double totalAmount = Double.parseDouble(parts[6]);
    double fee = Double.parseDouble(parts[7]);
    LocalDateTime timestamp = LocalDateTime.parse(parts[8]);
    return Transaction.reloadFromStorage(id, portfolioId, stockSymbol, type,
        quantity, pricePerShare, totalAmount, fee, timestamp);
  }

  private String toPSV(Transaction transaction)
  {
    return transaction.getId() + "¤" + transaction.getPortfolioId() + "¤"
        + transaction.getStockSymbol() + "¤" + transaction.getType() + "¤"
        + transaction.getQuantity() + "¤" + transaction.getPricePerShare() + "¤"
        + transaction.getTotalAmount() + "¤" + transaction.getFee() + "¤"
        + transaction.getTimestamp();
  }

  public List<Transaction> getTransactionsAppendOnly()
  {
    return transactionBuffer;
  }

  // STOCK SPECIFIC METHODS

  public List<Stock> getStockList()
  {
    if (stockList == null)
    {
      stockList = new ArrayList<>();
      for (String line : readAllLines(directoryPath + "/stocklist.txt"))
      {
        stockList.add(stockFromPSV(line));
      }
    }
    return stockList;
  }

  private Stock stockFromPSV(String line)
  {
    String[] parts = splitPSV(line);
    return new Stock(parts[0], parts[1], Double.parseDouble(parts[2]),
        StockState.valueOf(parts[3]));
  }

  private String toPSV(Stock stock)
  {
    return stock.getSymbol() + "¤" + stock.getName() + "¤"
        + stock.getCurrentPrice() + "¤" + stock.getCurrentState();
  }

  // STOCKPRICEHISTORY SPECIFIC METHODS

  public List<StockPriceHistory> getStockPriceHistoryList()
  {
    if (stockPriceHistoryList == null)
    {
      stockPriceHistoryList = new ArrayList<>();
      for (String line : readAllLines(
          directoryPath + "/stockpricehistorylist.txt"))
      {
        stockPriceHistoryList.add(stockPriceHistoryFromPSV(line));
      }
    }
    return stockPriceHistoryList;
  }

  private StockPriceHistory stockPriceHistoryFromPSV(String line)
  {
    String[] parts = splitPSV(line);
    return StockPriceHistory.reloadFromStorage(parts[0], parts[1],
        Double.parseDouble(parts[2]), LocalDateTime.parse(parts[3]));
  }

  private String toPSV(StockPriceHistory stockPriceHistory)
  {
    return stockPriceHistory.getId() + "¤" + stockPriceHistory.getStockSymbol()
        + "¤" + stockPriceHistory.getPrice() + "¤"
        + stockPriceHistory.getTimestamp();
  }

  public List<StockPriceHistory> getStockPriceHistoryAppendOnly()
  {
    return stockPriceHistoryBuffer;
  }

  // HJÆLPEMETODER TIL LÆSNING OG SKRIVNING

  private List<String> readAllLines(String filePath)
  {
    try
    {
      return Files.readAllLines(Paths.get(filePath));
    }
    catch (IOException e)
    {
      Logger.getInstance()
          .log("ERROR", "Failed to read from file: " + filePath);
      throw new RuntimeException("Failed to read from file: " + filePath, e);
    }
  }

  private void writeAllLines(String filePath, List<String> lines)
  {
    try
    {
      Files.write(Paths.get(filePath), lines);
    }
    catch (IOException e)
    {
      Logger.getInstance().log("ERROR", "Failed to write to file: " + filePath);
      throw new RuntimeException("Failed to write to file: " + filePath, e);
    }
  }

  private String[] splitPSV(String line)
  {
    return line.split("¤");
  }

  private void appendLinesToFile(String filePath, List<String> list)
  {
    try
    {
      Files.write(Path.of(filePath), list, StandardOpenOption.CREATE,
          StandardOpenOption.APPEND);
    }
    catch (IOException e)
    {
      Logger.getInstance()
          .log("ERROR", "Failed to append to file: " + filePath);
      throw new RuntimeException("Failed to append to file: " + filePath, e);
    }
  }

  // HANDLINGER

  @Override public void beginTransaction()
  {
    portfolioList = null;
    ownedStockList = null;
    stockList = null;
    stockPriceHistoryList = null;
    transactionList = null;
    transactionBuffer.clear();
    stockPriceHistoryBuffer.clear();
  }

  @Override public void commit()
  {
    synchronized (FILE_WRITE_LOCK)
    {
      if (portfolioList != null)
      {
        List<String> lines = new ArrayList<>();
        for (Portfolio portfolio : portfolioList)
          lines.add(toPSV(portfolio));
        writeAllLines(directoryPath + "/portfoliolist.txt", lines);
      }

      if (ownedStockList != null)
      {
        List<String> lines = new ArrayList<>();
        for (OwnedStock ownedStock : ownedStockList)
          lines.add(toPSV(ownedStock));
        writeAllLines(directoryPath + "/ownedstocklist.txt", lines);
      }

      if (transactionList != null)
      {
        List<String> lines = new ArrayList<>();
        for (Transaction transaction : transactionList)
          lines.add(toPSV(transaction));
        writeAllLines(directoryPath + "/transactionlist.txt", lines);
      }

      if (stockList != null)
      {
        List<String> lines = new ArrayList<>();
        for (Stock stock : stockList)
          lines.add(toPSV(stock));
        writeAllLines(directoryPath + "/stocklist.txt", lines);
      }

      if (stockPriceHistoryList != null)
      {
        List<String> lines = new ArrayList<>();
        for (StockPriceHistory stockPriceHistory : stockPriceHistoryList)
          lines.add(toPSV(stockPriceHistory));
        writeAllLines(directoryPath + "/stockpricehistorylist.txt", lines);
      }

      if (!stockPriceHistoryBuffer.isEmpty())
      {
        List<String> lines = new ArrayList<>();
        for (StockPriceHistory stockPriceHistory : stockPriceHistoryBuffer)
        {
          lines.add(toPSV(stockPriceHistory));
        }
          appendLinesToFile(directoryPath + "/stockpricehistorylist.txt",
              lines);
      }

      if (!transactionBuffer.isEmpty())
      {
        List<String> lines = new ArrayList<>();
        for (Transaction transaction : transactionBuffer)
        {
          lines.add(toPSV(transaction));
        }
        appendLinesToFile(directoryPath + "/transactionlist.txt", lines);
      }
    }

    clearData();
  }

  private void clearData()
  {
    beginTransaction();
  }

  @Override public void rollback()
  {
    beginTransaction();
  }

}
