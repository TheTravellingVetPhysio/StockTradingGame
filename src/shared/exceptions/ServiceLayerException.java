package shared.exceptions;

public class ServiceLayerException extends RuntimeException
{
  public ServiceLayerException(String message, Throwable cause) {
    super(message, cause);
  }
}
