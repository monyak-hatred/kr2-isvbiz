package warehouse;

/** Неизменяемая запись об операции прихода или выдачи. */
public final class StockMovement {
    private final String itemCode;
    private final MovementType type;
    private final int quantity;

    public StockMovement(String itemCode, MovementType type, int quantity) {
        this.itemCode = itemCode;
        this.type = type;
        this.quantity = quantity;
    }
    public String getItemCode() { return itemCode; }
    public MovementType getType() { return type; }
    public int getQuantity() { return quantity; }
    @Override public String toString() { return type + " " + itemCode + " x" + quantity; }
}
