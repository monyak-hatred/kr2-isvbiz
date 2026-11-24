package delivery;

/** Доставка выбранным способом невозможна. */
public class DeliveryNotPossibleException extends RuntimeException {
    public DeliveryNotPossibleException(String message) {
        super(message);
    }
}
