package entities;

import shared.configuration.AppConfig;

import java.util.UUID;

public class Portfolio
{
  private final String id;
  private String name;
  private double currentBalance;

  private Portfolio(String id, String name, double currentBalance)
  {
    this.id = id;
    this.name = name;
    this.currentBalance = currentBalance;
  }

  public static Portfolio createNew(String name)   // Factory method
  {
    return new Portfolio(UUID.randomUUID().toString(), name, AppConfig.getInstance().getStartingBalance());
  }

  public static Portfolio reloadFromStorage(String id, String name, double currentBalance)    // Factory method
  {
   return new Portfolio(id, name, currentBalance);
  }

  public String getId()
  {
    return id;
  }

  public String getName()
  {
    return name;
  }

  public double getCurrentBalance()
  {
    return currentBalance;
  }

  public void setName(String name)
  {
    this.name = name;
  }

  public void setCurrentBalance(double transactionAmount)
  {
    this.currentBalance = currentBalance + transactionAmount;
  }
} // mabin@systematic.com
