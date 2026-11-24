package delivery;

/** Координирует расчёт: выбрать политику, проверить возможность, посчитать. Без ветвлений по способам. */
public class DeliveryService {
    private final DeliveryPolicyFactory factory;

    public DeliveryService() { this(new DeliveryPolicyFactory()); }
    public DeliveryService(DeliveryPolicyFactory factory) { this.factory = factory; }

    public double calculate(DeliveryRequest request) {
        if (request == null) throw new IllegalArgumentException("Запрос доставки не задан");
        DeliveryPolicy policy = factory.get(request.getType());
        if (!policy.supports(request)) {
            throw new DeliveryNotPossibleException("Доставка способом " + request.getType()
                    + " невозможна: допустимо " + policy.describeLimits());
        }
        return policy.calculateCost(request);   // полиморфный вызов
    }
}
