package shared.exceptions;

public class StockBankruptException extends RuntimeException
{
  public StockBankruptException(String stockSymbol)
  {
    super(stockSymbol + " is bankrupt and cannot be traded currently.");
  }
}
