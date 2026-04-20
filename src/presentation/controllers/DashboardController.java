package presentation.controllers;

import business.services.PortfolioService;
import business.services.StockListenerService;
import business.services.TransactionService;
import dto.TransactionResult;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import presentation.models.OwnedStockUI;
import presentation.viewmodels.DashboardViewModel;
import presentation.viewmodels.MainViewModel;

public class DashboardController
{
  @FXML private LineChart<Number, Number> worthChart;
  @FXML private Label balanceLabel;
  @FXML private Label worthLabel;
  @FXML private TableView<OwnedStockUI> ownedStockTable;
  @FXML private TableColumn<OwnedStockUI, String> symbolColumn;
  @FXML private TableColumn<OwnedStockUI, Number> sharesColumn;
  @FXML private TableColumn<OwnedStockUI, Number> priceColumn;
  @FXML private TableColumn<OwnedStockUI, Number> totalValueColumn;
  @FXML private TableColumn<OwnedStockUI, Void> sellColumn;

  private DashboardViewModel viewModel;

  public DashboardController(PortfolioService portfolioService,
      TransactionService transactionService,
      StockListenerService stockListenerService,
      MainViewModel mainViewModel)
  {
    this.viewModel = new DashboardViewModel(portfolioService, transactionService,
        stockListenerService, mainViewModel);
  }

  @FXML private void initialize()
  {
    NumberAxis xAxis = (NumberAxis) worthChart.getXAxis();
    xAxis.setTickLabelsVisible(false);
    NumberAxis yAxis = (NumberAxis) worthChart.getYAxis();
    yAxis.setForceZeroInRange(false);
    yAxis.setAutoRanging(true);

    worthChart.setData(viewModel.getWorthSeries());

    balanceLabel.textProperty().bind(
        Bindings.createStringBinding(
            () -> String.format("Balance: %.2f", viewModel.portfolioBalanceProperty().get()),
            viewModel.portfolioBalanceProperty()));

    worthLabel.textProperty().bind(
        Bindings.createStringBinding(
            () -> String.format("Total worth: %.2f", viewModel.portfolioWorthProperty().get()),
            viewModel.portfolioWorthProperty()));

    symbolColumn.setCellValueFactory(data -> data.getValue().symbolProperty());

    sharesColumn.setCellValueFactory(data -> data.getValue().numberOfSharesProperty());

    priceColumn.setCellValueFactory(data -> data.getValue().pricePerShareProperty());
    priceColumn.setCellFactory(col -> new TableCell<>()
    {
      @Override protected void updateItem(Number price, boolean empty)
      {
        super.updateItem(price, empty);
        setText(empty || price == null ? null : String.format("%.2f", price.doubleValue()));
      }
    });

    totalValueColumn.setCellValueFactory(data -> data.getValue().totalValueProperty());
    totalValueColumn.setCellFactory(col -> new TableCell<>()
    {
      @Override protected void updateItem(Number val, boolean empty)
      {
        super.updateItem(val, empty);
        setText(empty || val == null ? null : String.format("%.2f", val.doubleValue()));
      }
    });

    sellColumn.setCellFactory(col -> new TableCell<>()
    {
      private final Button btn = new Button("Sell");
      {
        btn.setOnAction(e -> {
          OwnedStockUI stock = getTableView().getItems().get(getIndex());
          showSellDialog(stock);
        });
        btn.setStyle("-fx-padding: 2 8 2 8;");
      }

      @Override protected void updateItem(Void item, boolean empty)
      {
        super.updateItem(item, empty);
        setGraphic(empty ? null : btn);
      }
    });

    ownedStockTable.setItems(viewModel.getOwnedStocksUI());
    ownedStockTable.setFixedCellSize(33);
    ownedStockTable.prefHeightProperty()
        .bind(ownedStockTable.fixedCellSizeProperty()
            .multiply(Bindings.size(ownedStockTable.getItems())).add(46));
    ownedStockTable.minHeightProperty().bind(ownedStockTable.prefHeightProperty());
    ownedStockTable.maxHeightProperty().bind(ownedStockTable.prefHeightProperty());
  }

  private void showSellDialog(OwnedStockUI stock)
  {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Sell " + stock.getSymbol());

    TextField amountField = new TextField();
    amountField.setPromptText("Number of shares");

    VBox content = new VBox(10, new Label("Amount:"), amountField);
    content.setPadding(new Insets(10));

    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.showAndWait().ifPresent(buttonType -> {
      if (buttonType == ButtonType.OK)
      {
        int amount = Integer.parseInt(amountField.getText());
        TransactionResult result = viewModel.sellStock(stock.getSymbol(), amount);
        Alert alert = new Alert(result.success() ? Alert.AlertType.INFORMATION : Alert.AlertType.WARNING);
        alert.setTitle(result.success() ? "Sale successful" : "Sale unsuccessful");
        alert.setHeaderText(null);
        alert.setContentText(result.message());
        alert.showAndWait();
      }
    });
  }
}