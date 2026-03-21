package shared.exceptions;

public class PortfolioNotFoundException extends RuntimeException
{
  public PortfolioNotFoundException(String portfolioId)
  {
    super(portfolioId + " could not be found.");
  }
}
