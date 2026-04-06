package presentation.core;

public enum Views
{
  MAINLAYOUT("Main"), DASHBOARD("Dashboard"), STOCKMARKET("StockMarket"), TRANSACTIONS(
    "Transactions");

  private final String view;

  Views(String view)
  {
    this.view = view;
  }

  public String getView()
  {
    return "/presentation/views/" + view + "View.fxml";
  }

}
