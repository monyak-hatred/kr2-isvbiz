package delivery;

/** Единый контракт способа доставки. */
public interface DeliveryPolicy {
    /** Можно ли доставить такой заказ этим способом (ограничения веса и расстояния). */
    boolean supports(DeliveryRequest request);

    /** Стоимость; вызывается только для запроса, который supports(). */
    double calculateCost(DeliveryRequest request);

    /** Человекочитаемое описание ограничений — для сообщения об ошибке. */
    String describeLimits();
}
