package sales;

import java.util.Comparator;

/**
 * Comparator с двумя полями. Вариант 5: поле вторичной сортировки — регион (по алфавиту).
 * Первичное: сумма по убыванию. Изменить вторичное поле = изменить одну строку здесь.
 */
public final class SaleComparators {
    private SaleComparators() { }

    public static final Comparator<Sale> AMOUNT_DESC_THEN_REGION =
            Comparator.comparingDouble(Sale::getAmount).reversed()
                      .thenComparing(Sale::getRegion);
}
