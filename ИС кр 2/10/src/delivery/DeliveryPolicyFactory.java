package delivery;

import java.util.EnumMap;
import java.util.Map;

/** Только выбор реализации по типу. Формул здесь нет. Новый способ = новый класс + одна строка регистрации. */
public class DeliveryPolicyFactory {
    private final Map<DeliveryType, DeliveryPolicy> policies = new EnumMap<>(DeliveryType.class);

    public DeliveryPolicyFactory() {
        policies.put(DeliveryType.PICKUP, new PickupPolicy());
        policies.put(DeliveryType.STANDARD, new StandardCourierPolicy());
        policies.put(DeliveryType.EXPRESS, new ExpressPolicy());
        policies.put(DeliveryType.LOCKER, new LockerPolicy());
    }

    public DeliveryPolicy get(DeliveryType type) {
        DeliveryPolicy p = type == null ? null : policies.get(type);
        if (p == null) {
            throw new DeliveryNotPossibleException("Для способа доставки " + type + " нет реализации");
        }
        return p;
    }
}
