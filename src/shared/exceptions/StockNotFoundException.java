package shared.exceptions;

public class StockNotFoundException extends RuntimeException
{
  public StockNotFoundException(String stockSymbol)
  {
    super(stockSymbol + " could not be found.");
  }
}
