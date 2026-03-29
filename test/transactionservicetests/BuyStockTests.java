package transactionservicetests;

import _mockups.dao.*;
import business.services.TransactionService;
import business.stockmarket.simulation.BankruptState;
import business.stockmarket.simulation.DecliningState;
import business.stockmarket.simulation.GrowingState;
import business.stockmarket.simulation.SteadyState;
import dto.StockBuyRequest;
import entities.Portfolio;
import entities.Stock;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import persistance.interfaces.UnitOfWork;
import shared.configuration.AppConfig;
import shared.exceptions.InsufficientFundsException;
import shared.exceptions.NotFoundException;
import shared.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;

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

  @ParameterizedTest @ValueSource(ints = {0,
      -1})      // BoundaryValueAnalysis - negativ
  void buyStock_QuantityLessThanOne_ThrowsIllegalArgumentException(
      int invalidQuantity)
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertThrows(IllegalArgumentException.class,
        () -> transactionService.buyStock(
            new StockBuyRequest(portfolioId, "VESTA", invalidQuantity)));
  }

  @ParameterizedTest @ValueSource(ints = {1,
      2})      // BoundaryValueAnalysis - positiv
  void buyStock_QuantityIsOneOrMore_ShouldNotThrow(int validQuantity)
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.buyStock(
        new StockBuyRequest(portfolioId, "VESTA", validQuantity)));
  }

  @Test void buyStock_StockExists_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.buyStock(
        new StockBuyRequest(portfolioId, "VESTA", 10)));
  }

  @Test void buyStock_StockDoesNotExists_ThrowsNotFoundException()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    // Act & Assert
    assertThrows(NotFoundException.class, () ->
    transactionService.buyStock(
        new StockBuyRequest(portfolioId, "VESTA", 10))
    );
  }

  @Test void buyStock_PortfolioExists_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.buyStock(
        new StockBuyRequest(portfolioId, "VESTA", 10)));
  }

  @Test void buyStock_PortfolioDoesNotExists_ThrowsNotFoundException()
  {
    // Arrange
    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertThrows(NotFoundException.class, () ->
        transactionService.buyStock(
            new StockBuyRequest("0000", "VESTA", 10))
    );
  }

  @Test void buyStock_InSteadyState_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.buyStock(
        new StockBuyRequest(portfolioId, "VESTA", 10)));
  }

  @Test void buyStock_InGrowingState_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, GrowingState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.buyStock(
        new StockBuyRequest(portfolioId, "VESTA", 10)));
  }

  @Test void buyStock_InDecliningState_ShouldNotThrow()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, DecliningState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertDoesNotThrow(() -> transactionService.buyStock(
        new StockBuyRequest(portfolioId, "VESTA", 10)));
  }

  @Test void buyStock_InBankruptState_ThrowsException()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, BankruptState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertThrows(Exception.class, () ->
        transactionService.buyStock(
            new StockBuyRequest(portfolioId, "VESTA", 10))
    );
  }

  @Test void buyStock_StockPriceIsZeroAndBankruptState_ThrowsException()
  {
    /*
    Hvis jeg kører denne uden at sætte BankruptState så kaster den ikke exception;
    er det korrekt at antage, at det skyldes at "ansvaret" for dette ligger et helt
    andet sted (TransitionManager), og for at undersøge om den mekanisme virker som
    det skal er det den klasse jeg bør teste?
    Og så er det fint her at gå ud fra at de to går hånd i hånd?
    */

    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 0, BankruptState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertThrows(Exception.class, () ->
        transactionService.buyStock(
            new StockBuyRequest(portfolioId, "VESTA", 10))
    );
  }

  @Test void buyStock_BuyNewStock_CreatesNewOwnedStock()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertEquals(1, mockOwnedStockDAO.getAllOwnedStocks().size());
  }

  @Test void buyStock_BuyNewStock_SetsCorrectNumberOfShares()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    String stockSymbol = stock.getSymbol();
    mockStockDAO.createStock(stock);

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertEquals(10,
        mockOwnedStockDAO.getOwnedStockByPortfolioIdAndSymbol(portfolioId,
            stockSymbol).getNumberOfShares());
  }

  @Test void buyStock_WithBalanceLargerThanTotalAmount_PortfolioBalanceDeductsTotalAmount()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew(
        "Test");  // bliver oprettet med balance på 10.000
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // System.out.println(portfolio.getCurrentBalance());
    // System.out.println(mockPortfolioDAO.getPortfolioById(portfolioId).getCurrentBalance());

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertEquals((900 - AppConfig.getInstance().getTransactionFee()),
        portfolio.getCurrentBalance());
  }

  @Test void buyStock_WithBalanceSameAsTotalAmount_BalanceBecomesZero()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew(
        "Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 95));     // Transaction Fee = 50

    // Assert
    assertEquals(0,
        portfolio.getCurrentBalance());
  }

  @Test void buyStock_WithInsufficientFunds_ThrowsInsufficientFundsException()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act & Assert
    assertThrows(InsufficientFundsException.class,
        () -> transactionService.buyStock(
            new StockBuyRequest(portfolioId, "VESTA", 100)));       // Should be insuffiecient because of the transaction fee on top
  }

  @Test void buyStock_BuyAlreadyOwnedStock_DoesNotCreateDuplicateOwnedStock()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    String stockSymbol = stock.getSymbol();
    mockStockDAO.createStock(stock);

    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 5));

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertEquals(1, mockOwnedStockDAO.getAllOwnedStocks().size());
  }

  @Test void buyStock_BuyAlreadyOwnedStock_SetsCorrectNumberOfShares()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    String stockSymbol = stock.getSymbol();
    mockStockDAO.createStock(stock);

    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 5));

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertEquals(15,
        mockOwnedStockDAO.getOwnedStockByPortfolioIdAndSymbol(portfolioId,
            stockSymbol).getNumberOfShares());
  }

  @Test void buyStock_BuyMultipleStock_CreatesNewOwnedStocksPerStock()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock vesta = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    Stock novo = new Stock("NOVO", "Novo", 10, SteadyState.NAME, 1);

    mockStockDAO.createStock(vesta);
    mockStockDAO.createStock(novo);

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));
    transactionService.buyStock(new StockBuyRequest(portfolioId, "NOVO", 10));

    // Assert
    assertEquals(2, mockOwnedStockDAO.getAllOwnedStocks().size());
  }

  @Test void buyStock_CreatesTransaction()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertEquals(1, mockTransactionDAO.getAllTransactions().size());
  }

  @Test void buyStock_FeeIsAddedToTransaction_FeeAmountIsFoundInSavedTransaction()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    double expectedFee = AppConfig.getInstance().getTransactionFee();

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertEquals(expectedFee, mockTransactionDAO.getAllTransactions().getFirst().getFee());
    assertEquals(1000 - (10 * 10 + expectedFee), portfolio.getCurrentBalance());
  }


  @Test
  void buyStock_SuccessfulPurchase_CallsCommit()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(1000);
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act
    transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10));

    // Assert
    assertTrue(((MockUnitOfWork)uow).commitCalled);
    assertFalse(((MockUnitOfWork)uow).rollbackCalled);
  }


  @Test
  void buyStock_FailureThrown_CallsRollback()
  {
    // Arrange
    Portfolio portfolio = Portfolio.createNew("Test");
    portfolio.setCurrentBalance(0); // Throws InsufficientFundsException
    mockPortfolioDAO.createPortfolio(portfolio);
    String portfolioId = portfolio.getId();

    Stock stock = new Stock("VESTA", "Vestas", 10, SteadyState.NAME, 1);
    mockStockDAO.createStock(stock);

    // Act
    assertThrows(InsufficientFundsException.class, () ->
        transactionService.buyStock(new StockBuyRequest(portfolioId, "VESTA", 10))
    );

    // Assert
    assertTrue(((MockUnitOfWork)uow).rollbackCalled);
    assertFalse(((MockUnitOfWork)uow).commitCalled);
  }

}

