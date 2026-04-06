package presentation.viewmodels;

import business.services.PortfolioService;
import entities.Portfolio;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MainViewModel
{
  private PortfolioService portfolioService;
  private final ObjectProperty<Portfolio> selectedPortfolio = new SimpleObjectProperty<>();
  private final ObservableList<Portfolio> portfolios = FXCollections.observableArrayList();

  public MainViewModel(PortfolioService portfolioService) {
    this.portfolioService = portfolioService;
    portfolios.addAll(portfolioService.getAllPortfolios());
  }

  public Portfolio getSelectedPortfolio()
  {
    return selectedPortfolio.get();
  }

  public ObjectProperty<Portfolio> selectedPortfolioProperty()
  {
    return selectedPortfolio;
  }

  public void setSelectedPortfolio(Portfolio portfolio)
  {
    selectedPortfolio.set(portfolio);
  }

  public ObservableList<Portfolio> getPortfolios()
  {
    return portfolios;
  }
}
