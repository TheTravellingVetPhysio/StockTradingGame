package persistance.interfaces;

public interface UnitOfWork
{

  void beginTransaction();
  void commit();
  void rollback();
}
