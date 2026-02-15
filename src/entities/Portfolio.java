package entities;

import shared.configuration.AppConfig;

public class Portfolio
{
  private final int id;
  private double currentBalance;
  private static int idCounter = 0;

  public Portfolio()
  {
    currentBalance = AppConfig.getInstance().getStartingBalance();
    id = idCounter++;
  }

  public int getId()
  {
    return id;
  }

  public double getCurrentBalance()
  {
    return currentBalance;
  }

  public void setCurrentBalance(double transactionAmount)
  {
    this.currentBalance = currentBalance + transactionAmount;
  }
}
