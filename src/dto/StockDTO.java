package dto;

public record StockDTO(String symbol, String name, double currentPrice,
                       String state)
{
}