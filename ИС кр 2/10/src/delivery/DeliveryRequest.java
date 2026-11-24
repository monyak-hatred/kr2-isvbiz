package delivery;

/** Параметры доставки. Неизменяем; данные проверяются при создании. */
public final class DeliveryRequest {
    private final DeliveryType type;
    private final double distanceKm;
    private final double weightKg;

    public DeliveryRequest(DeliveryType type, double distanceKm, double weightKg) {
        if (type == null) throw new IllegalArgumentException("Способ доставки не задан");
        if (Double.isNaN(distanceKm) || Double.isInfinite(distanceKm) || distanceKm < 0)
            throw new IllegalArgumentException("Расстояние должно быть >= 0: " + distanceKm);
        if (Double.isNaN(weightKg) || Double.isInfinite(weightKg) || weightKg <= 0)
            throw new IllegalArgumentException("Вес должен быть > 0: " + weightKg);
        this.type = type;
        this.distanceKm = distanceKm;
        this.weightKg = weightKg;
    }
    public DeliveryType getType() { return type; }
    public double getDistanceKm() { return distanceKm; }
    public double getWeightKg() { return weightKg; }
}
