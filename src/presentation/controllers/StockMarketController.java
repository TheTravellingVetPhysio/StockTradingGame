package presentation.controllers;

import business.services.StockListenerService;
import business.services.StockMarketService;
import business.services.TransactionService;
import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.CheckBoxTableCell;
import presentation.models.StockUI;
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

  private StockMarketViewModel stockMarketViewModel;

  public StockMarketController(StockMarketService stockMarketService,
      TransactionService transactionService,
      StockListenerService stockListenerService)
  {
    this.stockMarketViewModel = new StockMarketViewModel(stockMarketService,
        transactionService, stockListenerService);
  }

  @FXML private void initialize()
  {
    stockTable.setEditable(true);
    selectedColumn.setEditable(true);

    NumberAxis xAxis = (NumberAxis) stockChart.getXAxis();
    xAxis.setTickLabelsVisible(false);

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

    selectedColumn.setCellValueFactory(
        data -> data.getValue().selectedProperty());
    selectedColumn.setCellFactory(
        CheckBoxTableCell.forTableColumn(selectedColumn));

    stockTable.setItems(stockMarketViewModel.getStocksUI());

    stockTable.setFixedCellSize(33);
    stockTable.prefHeightProperty().bind(stockTable.fixedCellSizeProperty()
        .multiply(Bindings.size(stockTable.getItems())).add(46));
    stockTable.minHeightProperty().bind(stockTable.prefHeightProperty());
    stockTable.maxHeightProperty().bind(stockTable.prefHeightProperty());

    stockMarketViewModel.getStocksUI().forEach(ui -> {
      ui.selectedProperty().addListener((obs, oldV, newV) -> {
        updateChart();
      });
    });

    updateChart();
    stockMarketViewModel.setOnChartDataUpdated(() -> updateChart());
  }

  private void updateChart()
  {
    stockChart.getData().clear();
    stockChart.getData().addAll(stockMarketViewModel.buildSelectedSeries());

    // Auto-skalering
    NumberAxis yAxis = (NumberAxis) stockChart.getYAxis();
    yAxis.setAutoRanging(false);
    yAxis.setForceZeroInRange(false);
    yAxis.setLowerBound(Double.NaN);
    yAxis.setUpperBound(Double.NaN);
    yAxis.setAutoRanging(true);

  }

  @FXML private void onSevenTicksClicked()
  {
    stockMarketViewModel.setTickRange(7);
    updateChart();
  }

  @FXML private void onThirtyTicksClicked()
  {
    stockMarketViewModel.setTickRange(30);
    updateChart();
  }

  @FXML private void onThreeSixFiveTicksClicked()
  {
    stockMarketViewModel.setTickRange(365);
    updateChart();
  }

  @FXML private void onEighteenTwentyFiveTicksClicked()
  {
    stockMarketViewModel.setTickRange(1825);
    updateChart();
  }

}
