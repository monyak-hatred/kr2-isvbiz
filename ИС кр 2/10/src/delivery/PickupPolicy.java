package delivery;

/** Самовывоз: бесплатно, вес до 50 кг. */
public class PickupPolicy implements DeliveryPolicy {
    private static final double MAX_WEIGHT = 50;

    @Override public boolean supports(DeliveryRequest r) { return r.getWeightKg() <= MAX_WEIGHT; }
    @Override public double calculateCost(DeliveryRequest r) { return 0.0; }
    @Override public String describeLimits() { return "вес до " + MAX_WEIGHT + " кг"; }
}
