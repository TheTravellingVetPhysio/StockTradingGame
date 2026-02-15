import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

public class RunApp
{
  public static void main(String[] args)
  {
    Logger logger = Logger.getInstance();
    logger.setOutput(new ConsoleLogOutput());

    logger.log("INFO", "Application started");
    logger.log("WARNING", "Stock not found in database");
    logger.log("ERROR", "Failed to save data...");
  }
}
