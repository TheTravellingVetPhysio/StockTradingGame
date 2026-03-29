package transactionservicetests;

import _mockups.dao.*;
import business.services.TransactionService;
import business.stockmarket.simulation.*;
import dto.StockBuyRequest;
import dto.StockSellRequest;
import entities.OwnedStock;
import entities.Portfolio;
import entities.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import persistance.interfaces.UnitOfWork;
import shared.configuration.AppConfig;
import shared.exceptions.NotFoundException;
import shared.exceptions.StockBankruptException;
import shared.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

public class SellStockTests
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

  // QUANTITY VALIDATION (BVA)

  @ParameterizedTest @ValueSource(ints = {0,
      -1}) void sellStock_QuantityLessThanOne_ThrowsIllegalArgumentException(
      int invalidQuantity)
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> transactionService.sellStock(
            new StockSellRequest(portfolio.getId(), "VESTA", invalidQuantity)));
  }

  @ParameterizedTest @ValueSource(ints = {1,
      2}) void sellStock_QuantityValid_ShouldNotThrow(int validQuantity)
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", validQuantity)));
  }

  // NOT FOUND EXCEPTIONS

  @Test void sellStock_StockExists_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.buyStock(
        new StockBuyRequest(portfolio.getId(), "VESTA", 3)));
  }

  @Test void sellStock_StockDoesNotExist_ThrowsNotFoundException()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertThrows(NotFoundException.class, () -> transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3)));
  }

  @Test void sellStock_PortfolioExists_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3)));
  }

  @Test void sellStock_PortfolioDoesNotExists_ThrowsNotFoundException()
  {
    // Arrange
    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew("0000", "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertThrows(NotFoundException.class, () -> transactionService.sellStock(
        new StockSellRequest("0000", "VESTA", 3)));
  }

  // STOCK STATE

  @Test void sellStock_InSteadyState_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3)));
  }

  @Test void sellStock_InGrowingState_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, GrowingState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3)));
  }

  @Test void sellStock_InDecliningState_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, DecliningState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3)));
  }

  @Test void sellStock_StockInBankruptState_ThrowsStockBankruptException()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, BankruptState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertThrows(StockBankruptException.class,
        () -> transactionService.sellStock(
            new StockSellRequest(portfolio.getId(), "VESTA", 3)));
  }

  // OWNED STOCK AND SHARE VALIDATION

  @Test void sellStock_NoOwnedStock_ThrowsIllegalArgumentException()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> transactionService.sellStock(
            new StockSellRequest(portfolio.getId(), "VESTA", 3)));
  }

  @ParameterizedTest @ValueSource(ints = {11,
      12}) void sellStock_NotEnoughSharesInPortfolio_ThrowsIllegalArgumentException(
      int invalidQuantity)
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> transactionService.sellStock(
            new StockSellRequest(portfolio.getId(), "VESTA", invalidQuantity)));
  }

  @ParameterizedTest @ValueSource(ints = {9,
      10}) void sellStock_EnoughSharesInPortfolio_ShouldNotThrow(int validQuantity)
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", validQuantity)));
  }

  // SELL OPERATIONS

  @Test void sellStock_SellsSomeButNotAll_UpdatesNumberOfShares()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act
    transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 9));

    // Assert
    assertEquals(1,
        mockOwnedStockDAO.getOwnedStockByPortfolioIdAndSymbol(portfolio.getId(),
            "VESTA").getNumberOfShares());
  }

  @Test void sellStock_SellsAllShares_DeletesOwnedStock()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act
    transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 10));

    // Assert
    assertEquals(0, mockOwnedStockDAO.getAllOwnedStocks().size());
  }

  // PORTFOLIO BALANCE UPDATES AND TRANSACTIONS

  @Test void sellStock_BalanceUpdatedCorrectly()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    double fee = AppConfig.getInstance().getTransactionFee();

    // Act
    transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3));

    // Assert
    // + (price * quantity - fee)
    assertEquals(1000 + (10 * 3 - fee), portfolio.getCurrentBalance());
  }

  @Test void sellStock_CreatesTransaction()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act
    transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3));

    // Assert
    assertEquals(1, mockTransactionDAO.getAllTransactions().size());
  }

  @Test void sellStock_FeeIsAddedToTransaction()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    double fee = AppConfig.getInstance().getTransactionFee();

    // Act
    transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3));

    // Assert
    assertEquals(fee,
        mockTransactionDAO.getAllTransactions().getFirst().getFee());
  }

  // UNIT OF WORK

  @Test void sellStock_SuccessfulSale_CallsCommit()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    OwnedStock owned = OwnedStock.createNew(portfolio.getId(), "VESTA", 10);
    mockOwnedStockDAO.createOwnedStock(owned);

    // Act
    transactionService.sellStock(
        new StockSellRequest(portfolio.getId(), "VESTA", 3));

    // Assert
    assertTrue(((MockUnitOfWork) uow).commitCalled);
    assertFalse(((MockUnitOfWork) uow).rollbackCalled);
  }

  @Test void sellStock_FailureThrown_CallsRollback()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);

    // Create stock, but no owned stock → failure
    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act
    assertThrows(IllegalArgumentException.class,
        () -> transactionService.sellStock(
            new StockSellRequest(portfolio.getId(), "VESTA", 3)));

    // Assert
    assertTrue(((MockUnitOfWork) uow).rollbackCalled);
    assertFalse(((MockUnitOfWork) uow).commitCalled);
  }

}