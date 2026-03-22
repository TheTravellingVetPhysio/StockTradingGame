package entities;

public class Stock
{
  private final String symbol;
  private String name;
  private double currentPrice;
  private String currentState;
  private double riskProfile;

  public Stock(String symbol, String name, double currentPrice,
      String currentState, double riskProfile)
  {
    this.symbol = symbol;
    this.name = name;
    this.currentPrice = currentPrice;
    this.currentState = currentState;
    this.riskProfile = riskProfile;
  }

  public String getSymbol()
  {
    return symbol;
  }

  public String getName()
  {
    return name;
  }

  public double getCurrentPrice()
  {
    return currentPrice;
  }

  public String getCurrentState()
  {
    return currentState;
  }

  public double getRiskProfile()
  {
    return riskProfile;
  }

  public void setCurrentPrice(double currentPrice)
  {
    this.currentPrice = currentPrice;
  }

  public void setCurrentState(String currentState)
  {
    this.currentState = currentState;
  }
}
