package delivery;

/** Экспресс: 40 + 8*км + 12*кг (не менее минимума); вес до 20 кг, расстояние до 100 км. */
public class ExpressPolicy implements DeliveryPolicy {
    private static final double BASE = 40, PER_KM = 8, PER_KG = 12, MAX_WEIGHT = 20, MAX_KM = 100;

    @Override public boolean supports(DeliveryRequest r) {
        return r.getWeightKg() <= MAX_WEIGHT && r.getDistanceKm() <= MAX_KM;
    }
    @Override public double calculateCost(DeliveryRequest r) {
        return DeliveryRules.applyMinimum(BASE + PER_KM * r.getDistanceKm() + PER_KG * r.getWeightKg());
    }
    @Override public String describeLimits() { return "вес до " + MAX_WEIGHT + " кг, расстояние до " + MAX_KM + " км"; }
}
