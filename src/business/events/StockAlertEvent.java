package business.events;

import entities.Stock;

public record StockAlertEvent(Object arg, String message) {}