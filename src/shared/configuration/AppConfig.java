package shared.configuration;

public class AppConfig
{
  private static final AppConfig instance = new AppConfig();

  private final int startingBalance;
  private final double transactionFee;
  private final int updateFrequencyInMs;

  private AppConfig()
  {
    this.transactionFee = 50;
    this.updateFrequencyInMs = 100;
    this.startingBalance = 10000;
  }

  public static AppConfig getInstance()
  {
    return instance;
  }

  public int getStartingBalance()
  {
    return startingBalance;
  }

  public double getTransactionFee()
  {
    return transactionFee;
  }

  public int getUpdateFrequencyInMs()
  {
    return updateFrequencyInMs;
  }
}
