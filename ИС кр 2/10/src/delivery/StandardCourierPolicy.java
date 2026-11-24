package delivery;

/** Стандартный курьер: 10 + 3*км + 8*кг (не менее минимума); вес до 30 кг, расстояние до 500 км. */
public class StandardCourierPolicy implements DeliveryPolicy {
    private static final double BASE = 10, PER_KM = 3, PER_KG = 8, MAX_WEIGHT = 30, MAX_KM = 500;

    @Override public boolean supports(DeliveryRequest r) {
        return r.getWeightKg() <= MAX_WEIGHT && r.getDistanceKm() <= MAX_KM;
    }
    @Override public double calculateCost(DeliveryRequest r) {
        return DeliveryRules.applyMinimum(BASE + PER_KM * r.getDistanceKm() + PER_KG * r.getWeightKg());
    }
    @Override public String describeLimits() { return "вес до " + MAX_WEIGHT + " кг, расстояние до " + MAX_KM + " км"; }
}
