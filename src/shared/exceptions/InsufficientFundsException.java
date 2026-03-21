package shared.exceptions;

public class InsufficientFundsException extends RuntimeException
{
  public InsufficientFundsException(String portfolioId)
  {
    super("Insufficient funds on " + portfolioId);
  }
}
