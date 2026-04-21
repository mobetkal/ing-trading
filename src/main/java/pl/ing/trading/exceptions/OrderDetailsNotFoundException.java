package pl.ing.trading.exceptions;

public class OrderDetailsNotFoundException extends RuntimeException {
    public OrderDetailsNotFoundException(String message) {
        super(message);
    }
}
