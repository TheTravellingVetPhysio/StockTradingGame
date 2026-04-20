package presentation.controllers;

import business.services.StockListenerService;
import business.services.StockMarketService;
import business.services.TransactionService;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.geometry.Insets;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.*;
import javafx.scene.control.cell.CheckBoxTableCell;
import javafx.scene.layout.VBox;
import presentation.models.StockUI;
import presentation.viewmodels.MainViewModel;
import presentation.viewmodels.StockMarketViewModel;

public class StockMarketController
{
  @FXML private LineChart<Number, Number> stockChart;
  @FXML private TableView<StockUI> stockTable;
  @FXML private TextField searchField;
  @FXML private TableColumn<StockUI, Boolean> selectedColumn;
  @FXML private TableColumn<StockUI, String> symbolColumn;
  @FXML private TableColumn<StockUI, String> nameColumn;
  @FXML private TableColumn<StockUI, String> stateColumn;
  @FXML private TableColumn<StockUI, Double> priceColumn;
  @FXML private TableColumn<StockUI, Void> buyColumn;
  @FXML private TableColumn<StockUI, Void> sellColumn;

  private StockMarketViewModel stockMarketViewModel;

  public StockMarketController(StockMarketService stockMarketService,
      TransactionService transactionService,
      StockListenerService stockListenerService, MainViewModel mainViewModel)
  {
    this.stockMarketViewModel = new StockMarketViewModel(stockMarketService,
        transactionService, stockListenerService, mainViewModel);
  }

  @FXML private void initialize()
  {
    stockTable.setEditable(true);
    selectedColumn.setEditable(true);

    NumberAxis xAxis = (NumberAxis) stockChart.getXAxis();
    xAxis.setTickLabelsVisible(false);

    NumberAxis yAxis = (NumberAxis) stockChart.getYAxis();
    yAxis.setForceZeroInRange(false);
    yAxis.setAutoRanging(true);

    symbolColumn.setCellValueFactory(data -> data.getValue().symbolProperty());
    nameColumn.setCellValueFactory(data -> data.getValue().nameProperty());

    stateColumn.setCellValueFactory(
        data -> data.getValue().displayStateProperty());

    priceColumn.setCellValueFactory(
        data -> data.getValue().priceProperty().asObject());
    priceColumn.setCellFactory(col -> new TableCell<>()
    {
      @Override protected void updateItem(Double price, boolean empty)
      {
        super.updateItem(price, empty);
        if (empty || price == null)
          setText(null);
        else
          setText(String.format("%.2f", price));
      }
    });

    buyColumn.setCellFactory(col -> new TableCell<>() {
      private final Button btn = new Button("Buy");
      {
        btn.setOnAction(e -> {
          StockUI stock = getTableView().getItems().get(getIndex());
          showBuyDialog(stock);
        });
             btn.setStyle("-fx-padding: 2 8 2 8;");
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : btn);
      }
    });

    sellColumn.setCellFactory(col -> new TableCell<>() {
      private final Button btn = new Button("Sell");
      {
        btn.setOnAction(e -> {
          StockUI stock = getTableView().getItems().get(getIndex());
          showSellDialog(stock);
        });
        btn.setStyle("-fx-padding: 2 8 2 8;");
      }

      @Override
      protected void updateItem(Void item, boolean empty) {
        super.updateItem(item, empty);
        setGraphic(empty ? null : btn);
      }
    });

    selectedColumn.setCellValueFactory(
        data -> data.getValue().selectedProperty());
    selectedColumn.setCellFactory(
        CheckBoxTableCell.forTableColumn(selectedColumn));

    stockTable.setItems(stockMarketViewModel.getStocksUI());
    stockTable.setFixedCellSize(33);
    stockTable.prefHeightProperty()
        .bind(stockTable.fixedCellSizeProperty()
        .multiply(Bindings.size(stockTable.getItems())).add(46));
    stockTable.minHeightProperty().bind(stockTable.prefHeightProperty());
    stockTable.maxHeightProperty().bind(stockTable.prefHeightProperty());

    stockMarketViewModel.getStocksUI().forEach(ui -> {
      ui.selectedProperty().addListener((obs, oldV, newV) -> {
        stockMarketViewModel.refreshSelectedSeries();
      });
    });

    stockChart.setData(stockMarketViewModel.getSelectedSeries());
    stockMarketViewModel.refreshSelectedSeries();
  }

  @FXML private void onSevenTicksClicked()
  {
    stockMarketViewModel.setTickRange(7);
    stockMarketViewModel.refreshSelectedSeries();
  }

  @FXML private void onThirtyTicksClicked()
  {
    stockMarketViewModel.setTickRange(30);
    stockMarketViewModel.refreshSelectedSeries();
  }

  @FXML private void onThreeSixFiveTicksClicked()
  {
    stockMarketViewModel.setTickRange(365);
    stockMarketViewModel.refreshSelectedSeries();
  }

  @FXML private void onEighteenTwentyFiveTicksClicked()
  {
    stockMarketViewModel.setTickRange(1825);
    stockMarketViewModel.refreshSelectedSeries();
  }

  private void showBuyDialog(StockUI stock) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Buy " + stock.getSymbol());

    TextField amountField = new TextField();
    amountField.setPromptText("Number of shares");

    Label priceLabel = new Label("Price per share: " + stock.getPrice());

    VBox content = new VBox(10,
        priceLabel,
        new Label("Number:"),
        amountField
    );
    content.setPadding(new Insets(10));

    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.showAndWait().ifPresent(result -> {
      if (result == ButtonType.OK) {
        int amount = Integer.parseInt(amountField.getText());
        String error = stockMarketViewModel.buyStock(stock.getSymbol(), amount);
        if (error != null) {
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("Purchase unsuccesfull");
          alert.setHeaderText(null);
          alert.setContentText(error);
          alert.showAndWait();
        }      }
    });
  }

  private void showSellDialog(StockUI stock) {
    Dialog<ButtonType> dialog = new Dialog<>();
    dialog.setTitle("Sell " + stock.getSymbol());

    TextField amountField = new TextField();
    amountField.setPromptText("Number of shares");

    Label priceLabel = new Label("Price per share: " + stock.getPrice());

    VBox content = new VBox(10,
        priceLabel,
        new Label("Amount:"),
        amountField
    );
    content.setPadding(new Insets(10));

    dialog.getDialogPane().setContent(content);
    dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);

    dialog.showAndWait().ifPresent(result -> {
      if (result == ButtonType.OK) {
        int amount = Integer.parseInt(amountField.getText());
        String error = stockMarketViewModel.sellStock(stock.getSymbol(), amount);
        if (error != null) {
          Alert alert = new Alert(Alert.AlertType.WARNING);
          alert.setTitle("Sale unsuccesfull");
          alert.setHeaderText(null);
          alert.setContentText(error);
          alert.showAndWait();
        }      }
    });
  }

}
