package presentation.core;

import entities.Portfolio;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import shared.logging.Logger;

public class ViewManager
{
  private final Stage stage;
  private final ControllerFactory controllerFactory;
  private BorderPane mainLayout;

  public ViewManager(Stage stage, ApplicationContext applicationContext)
  {
    this.stage = stage;
    this.controllerFactory = new ControllerFactory(applicationContext);
  }

  public void showMainView()
  {
    try
    {
      // Opretter en loader og fortæller hvis FXML filen dertil befinder sig (i Resources mappen):
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource(Views.MAINLAYOUT.getView()));

      // Fortællet loaderen at den skal bruge ControllerFactory i stedet for sin egen controller:
      loader.setControllerFactory(controllerFactory);

      // FXML-filen bliver loadet og UI elementerne oprettet m.m.:
      Parent root = loader.load();

      // Caster root til BorderPane, så jeg kan bruge showContent senere:
      mainLayout = (BorderPane) root;

      // Laver en scene - indholdet inden i stage (stage -> scene -> borderpane):
      Scene scene = new Scene(root);
      stage.setScene(scene);
      scene.getStylesheets()
          .add(getClass().getResource("/styles.css").toExternalForm());
      stage.show();
    }

    catch (Exception e)
    {
      e.printStackTrace();
      Logger.getInstance().log("ERROR",
          "Could not load view: " + Views.MAINLAYOUT.toString() + " - "
              + e.getMessage());
    }
  }

  public void showContent(Views view, Portfolio portfolio)
  {
    try
    {
      FXMLLoader loader = new FXMLLoader(
          getClass().getResource(view.getView()));
      loader.setControllerFactory(controllerFactory);
      Parent content = loader.load();

      if (portfolio != null)
      {
        Object controller = loader.getController();
        if (controller instanceof PortfolioDependant portfolioDependant)
        {
          portfolioDependant.setPortfolio(portfolio);
        }
      }

      ScrollPane scrollPane = (javafx.scene.control.ScrollPane) mainLayout.getCenter();
      VBox contentArea = (VBox) scrollPane.getContent();
      contentArea.getChildren().setAll(content);    }

    catch (Exception e)
    {
      e.printStackTrace();
      Logger.getInstance().log("ERROR",
          "Could not load view: " + view.toString() + " - " + e.getMessage());
    }
  }

}
