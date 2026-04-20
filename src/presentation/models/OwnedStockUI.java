package presentation.models;

import javafx.beans.property.*;

public class OwnedStockUI
{
  private final StringProperty symbol = new SimpleStringProperty();
  private final IntegerProperty numberOfShares = new SimpleIntegerProperty();
  private final DoubleProperty pricePerShare = new SimpleDoubleProperty();
  private final DoubleProperty totalValue = new SimpleDoubleProperty();

  public OwnedStockUI(String symbol, int numberOfShares, double pricePerShare)
  {
    this.symbol.set(symbol);
    this.numberOfShares.set(numberOfShares);
    this.pricePerShare.set(pricePerShare);
    this.totalValue.set(numberOfShares * pricePerShare);
  }

  public void update(int numberOfShares, double pricePerShare)
  {
    this.numberOfShares.set(numberOfShares);
    this.pricePerShare.set(pricePerShare);
    this.totalValue.set(numberOfShares * pricePerShare);
  }

  public StringProperty symbolProperty() { return symbol; }
  public IntegerProperty numberOfSharesProperty() { return numberOfShares; }
  public DoubleProperty pricePerShareProperty() { return pricePerShare; }
  public DoubleProperty totalValueProperty() { return totalValue; }
  public String getSymbol() { return symbol.get(); }
}