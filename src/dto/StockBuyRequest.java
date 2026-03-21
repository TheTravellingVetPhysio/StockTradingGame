package dto;

public record StockBuyRequest
    (String portfolioId, String stockSymbol, int quantity)
{
}
