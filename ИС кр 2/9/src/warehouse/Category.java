package warehouse;

/**
 * Категории оборудования. Вариант 5: дополнительная категория NETWORK с лимитом остатка 30 единиц на позицию.
 * Лимит каждой категории хранится здесь — единственное место правила.
 */
public enum Category {
    COMPUTERS, PERIPHERALS, FURNITURE, CONSUMABLES,
    NETWORK(30);

    private final int maxStock;

    Category() { this(Integer.MAX_VALUE); }
    Category(int maxStock) { this.maxStock = maxStock; }

    /** Максимально допустимый остаток одной позиции этой категории. */
    public int getMaxStock() { return maxStock; }
}
