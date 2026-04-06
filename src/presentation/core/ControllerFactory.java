package presentation.core;

import javafx.util.Callback;

public class ControllerFactory implements Callback<Class<?>, Object>
{

  private final ApplicationContext applicationContext;

  public ControllerFactory(ApplicationContext applicationContext)
  {
    this.applicationContext = applicationContext;
  }

  @Override public Object call(Class<?> conterollerClass)
  {
    return applicationContext.getController(conterollerClass);
  }
}