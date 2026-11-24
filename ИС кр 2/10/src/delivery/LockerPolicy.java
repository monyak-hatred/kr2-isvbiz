package delivery;

/** Индивидуальный четвёртый способ: постамат. 5 + 2*км + 4*кг (не менее минимума); вес до 10 кг, расстояние до 50 км. */
public class LockerPolicy implements DeliveryPolicy {
    private static final double BASE = 5, PER_KM = 2, PER_KG = 4, MAX_WEIGHT = 10, MAX_KM = 50;

    @Override public boolean supports(DeliveryRequest r) {
        return r.getWeightKg() <= MAX_WEIGHT && r.getDistanceKm() <= MAX_KM;
    }
    @Override public double calculateCost(DeliveryRequest r) {
        return DeliveryRules.applyMinimum(BASE + PER_KM * r.getDistanceKm() + PER_KG * r.getWeightKg());
    }
    @Override public String describeLimits() { return "вес до " + MAX_WEIGHT + " кг, расстояние до " + MAX_KM + " км"; }
}
