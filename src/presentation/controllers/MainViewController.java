package presentation.controllers;

import business.services.PortfolioService;
import entities.Portfolio;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import presentation.core.ViewManager;
import presentation.core.Views;
import presentation.viewmodels.MainViewModel;

public class MainViewController
{
  private final MainViewModel viewModel;
  private final ViewManager viewManager;

  @FXML private HBox portfolioButtons;
  @FXML private VBox contentArea;

  public MainViewController(ViewManager viewManager,
      PortfolioService portfolioService)
  {
    this.viewManager = viewManager;
    this.viewModel = new MainViewModel(portfolioService);
  }

  @FXML
  private void initialize()
  {
    viewModel.getPortfolios().addListener((javafx.collections.ListChangeListener<Portfolio>) change ->
        rebuildPortfolioButtons()
    );
    rebuildPortfolioButtons();

    viewModel.selectedPortfolioProperty().addListener((obs, oldVal, newVal) ->
    {
      rebuildPortfolioButtons();
    });

    Platform.runLater(this::onStockMarketClicked);
  }

  @FXML private void onNewPortfolioClicked()
  {
    // TODO: Åbn popup, hvor man kan indtaste navn på ny portfolio
  }

  @FXML private void onStockMarketClicked()
  {
    viewManager.showContent(Views.STOCKMARKET, null);
  }

  @FXML private void onDashboardClicked() {
    viewManager.showContent(Views.DASHBOARD, viewModel.getSelectedPortfolio());
  }

  @FXML private void onTransactionsClicked() {
    viewManager.showContent(Views.TRANSACTIONS, viewModel.getSelectedPortfolio());
  }

  private void rebuildPortfolioButtons()
  {
    portfolioButtons.getChildren().clear();
    for (Portfolio portfolio : viewModel.getPortfolios())
    {
      Button btn = new Button(portfolio.getName());
      btn.setOnAction(e -> viewModel.setSelectedPortfolio(portfolio));

      if (portfolio.equals(viewModel.getSelectedPortfolio()))
        btn.getStyleClass().add("button-selected");

      portfolioButtons.getChildren().add(btn);
    }
  }
}
