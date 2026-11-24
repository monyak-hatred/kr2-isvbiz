package warehouse;

import java.util.Objects;

/** Карточка позиции. Идентичность — инвентарный код (equals/hashCode только по коду). */
public class StockItem {
    private final String code;
    private final String name;
    private final Category category;
    private int quantity;

    public StockItem(String code, String name, Category category) {
        this.code = requireText(code, "Инвентарный код");
        this.name = requireText(name, "Наименование");
        if (category == null) {
            throw new IllegalArgumentException("Категория не задана");
        }
        this.category = category;
    }

    private static String requireText(String v, String what) {
        if (v == null || v.trim().isEmpty()) {
            throw new IllegalArgumentException(what + " не может быть пустым");
        }
        return v.trim();
    }

    /** Приход. Изменяется только через Warehouse (package-private). */
    void increase(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Количество прихода должно быть положительным: " + amount);
        long result = (long) quantity + amount;
        if (result > category.getMaxStock()) {
            throw new IllegalStateException("Превышен лимит остатка для категории " + category + ": "
                    + category.getMaxStock() + " (было " + quantity + ", приход " + amount + ")");
        }
        quantity = (int) result;
    }

    /** Выдача. Остаток не может стать отрицательным. */
    void decrease(int amount) {
        if (amount <= 0) throw new IllegalArgumentException("Количество выдачи должно быть положительным: " + amount);
        if (amount > quantity) {
            throw new IllegalStateException("Недостаточно остатка для " + code + ": есть " + quantity + ", запрошено " + amount);
        }
        quantity -= amount;
    }

    public String getCode() { return code; }
    public String getName() { return name; }
    public Category getCategory() { return category; }
    public int getQuantity() { return quantity; }

    @Override public boolean equals(Object o) {
        return o instanceof StockItem && code.equals(((StockItem) o).code);
    }
    @Override public int hashCode() { return Objects.hash(code); }
    @Override public String toString() { return code + " " + name + " [" + category + "] " + quantity; }
}
