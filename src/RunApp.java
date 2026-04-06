import business.observertooling.EventType;
import business.services.*;
import business.stockmarket.MarketTickerThread;
import business.stockmarket.StockMarket;
import business.stockmarket.simulation.SteadyState;
import entities.Portfolio;
import entities.Stock;
import javafx.application.Application;
import javafx.stage.Stage;
import persistance.fileimplementation.*;
import persistance.interfaces.*;
import presentation.core.ApplicationContext;
import presentation.core.ViewManager;
import shared.configuration.AppConfig;
import shared.logging.ConsoleLogOutput;
import shared.logging.Logger;

import java.util.List;

public class RunApp extends Application
{
  public static void main(String[] args)
  {
    launch(args);
  }

  @Override public void start(Stage stage)
  {
    ApplicationContext applicationContext = ApplicationContext.getInstance();

    new GameService(applicationContext).initialize();

    ViewManager viewManager = new ViewManager(stage, applicationContext);
    applicationContext.setViewManager(viewManager);
    viewManager.showMainView();
  }

}
