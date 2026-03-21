package dto;

public record StockSellRequest
    (String portfolioId, String stockSymbol, int quantity)
{
}
