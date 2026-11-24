package sales;

/** Параметры аналитики в одном месте. */
public final class SalesRules {
    private SalesRules() { }
    /** Сделка крупная, если сумма >= порога (базовое значение; варианты 1, 4, 7 меняют его). */
    public static final double LARGE_SALE_THRESHOLD = 100_000;
    /** Вариант 5: параметр 30 — число записей в topSales по умолчанию (перегрузка без аргумента n). */
    public static final int DEFAULT_TOP_LIMIT = 30;
}
