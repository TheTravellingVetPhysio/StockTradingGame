package transactionservicetests;

import _mockups.dao.*;
import business.services.TransactionService;
import business.stockmarket.simulation.SteadyState;
import dto.StockBuyRequest;
import entities.Portfolio;
import entities.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import persistance.interfaces.UnitOfWork;
import shared.logging.Logger;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BuyStockTests
{
  MockPortfolioDAO mockPortfolioDAO;
  MockStockDAO mockStockDAO;
  MockOwnedStockDAO mockOwnedStockDAO;
  MockTransactionDAO mockTransactionDAO;
  UnitOfWork uow;

  TransactionService transactionService;

  @BeforeEach void setup()
  {
    mockPortfolioDAO = new MockPortfolioDAO();
    mockStockDAO = new MockStockDAO();
    mockOwnedStockDAO = new MockOwnedStockDAO();
    mockTransactionDAO = new MockTransactionDAO();
    uow = new MockUnitOfWork();

    transactionService = new TransactionService(uow, mockOwnedStockDAO,
        mockStockDAO, mockPortfolioDAO, mockTransactionDAO,
        Logger.getInstance());
  }

  @Test void buyStock_NewStockWithSufficientFunds_CreatesNewOwnedStock()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);
    portfolio.setCurrentBalance(1000);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertEquals(1, mockOwnedStockDAO.getAllOwnedStocks().size());
  }

}

