package _mockups.dao;

import persistance.interfaces.UnitOfWork;

public class MockUnitOfWork implements UnitOfWork
{
  @Override
  public void beginTransaction() {
    // gør ingenting
  }

  @Override
  public void commit() {
    // gør ingenting
  }

  @Override
  public void rollback() {
    // gør ingenting
  }
}