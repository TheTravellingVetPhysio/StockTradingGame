package business.events;

public record StockUpdateEvent(String symbol, String name, double price,
                               String state)
{
}