package presentation.models;

import entities.Stock;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;

public class StockUI
{
  private final StringProperty symbol = new SimpleStringProperty();
  private final StringProperty name = new SimpleStringProperty();
  private final DoubleProperty price = new SimpleDoubleProperty();
  private final StringProperty state = new SimpleStringProperty();
  private final StringProperty displayState = new SimpleStringProperty();
  private final BooleanProperty selected = new SimpleBooleanProperty(false);

  public StockUI(Stock stock)
  {
    symbol.set(stock.getSymbol());
    name.set(stock.getName());
    price.set(stock.getCurrentPrice());
    state.set(stock.getCurrentState());

    displayState.bind(
        Bindings.createStringBinding(() -> state.get().replace("State", ""),
            state));
  }

  // --- Getters til brug i TableView bindings ---
  public StringProperty symbolProperty()
  {
    return symbol;
  }

  public StringProperty nameProperty()
  {
    return name;
  }

  public DoubleProperty priceProperty()
  {
    return price;
  }

  public StringProperty stateProperty()
  {
    return state;
  }

  public StringProperty displayStateProperty()
  {
    return displayState;
  }

  public BooleanProperty selectedProperty()
  {
    return selected;
  }

  public boolean isSelected()
  {
    return selected.get();
  }

  public void setSelected(boolean value)
  {
    selected.set(value);
  }

  // Almindelige getters

  public String getSymbol()
  {
    return symbol.get();
  }

  public String getName()
  {
    return name.get();
  }

  public double getPrice()
  {
    return price.get();
  }

  public String getState()
  {
    return state.get();
  }

}
