package entities;

public class Portfolio
{
  private final int id;
  private double currentBalance;
  private static int idCounter = 0;

  public Portfolio()
  {
    currentBalance = 0;
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
