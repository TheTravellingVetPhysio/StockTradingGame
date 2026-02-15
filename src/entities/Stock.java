package entities;

public class Stock
{
  private final String symbol;
  private String name;
  private double currentPrice;
  private StockState currentState;

  public Stock(String symbol, String name, double currentPrice,
      StockState currentState)
  {
    this.symbol = symbol;
    this.name = name;
    this.currentPrice = currentPrice;
    this.currentState = currentState;
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

  public StockState getCurrentState()
  {
    return currentState;
  }
}
