package _mockups.dao;

import persistance.interfaces.UnitOfWork;

public class MockUnitOfWork implements UnitOfWork
{
  public boolean commitCalled = false;
  public boolean rollbackCalled = false;

  @Override public void beginTransaction()
  {
    // gør ingenting
  }

  @Override public void commit()
  {
    commitCalled = true;
  }

  @Override public void rollback()
  {
    rollbackCalled = true;
  }
}