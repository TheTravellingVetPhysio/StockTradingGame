package shared.logging;

public class Logger
{
  private static final Logger instance = new Logger();  // Eager initialization
  private LogOutput output;

  private Logger() {}

  public static Logger getInstance()
  {
    return instance;
  }

  public synchronized void log(String level, String message)
  {
    if (output != null)
    {
      output.log(level, message);
    }
  }

  public synchronized void setOutput(LogOutput output) {
    this.output = output;
  }
}
