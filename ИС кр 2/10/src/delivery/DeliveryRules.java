package delivery;

/** Общие правила стоимости. Вариант 5: минимальная стоимость платной доставки = 30. */
public final class DeliveryRules {
    private DeliveryRules() { }

    /** Минимальная стоимость любой платной доставки (самовывоз бесплатен и не подпадает под минимум). */
    public static final double MIN_COST = 30.0;

    /** Единственное место применения минимума. */
    public static double applyMinimum(double cost) {
        return Math.max(MIN_COST, cost);
    }
}
