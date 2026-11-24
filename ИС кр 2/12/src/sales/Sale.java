package sales;

/** Продажа. Неизменяема. */
public final class Sale {
    private final String id;
    private final String manager;
    private final String region;
    private final double amount;

    public Sale(String id, String manager, String region, double amount) {
        this.id = requireText(id, "id");
        this.manager = requireText(manager, "менеджер");
        this.region = requireText(region, "регион");
        if (Double.isNaN(amount) || Double.isInfinite(amount) || amount < 0) {
            throw new IllegalArgumentException("Сумма должна быть неотрицательным числом: " + amount);
        }
        this.amount = amount;
    }
    private static String requireText(String v, String what) {
        if (v == null || v.trim().isEmpty()) throw new IllegalArgumentException(what + " не может быть пустым");
        return v.trim();
    }
    public String getId() { return id; }
    public String getManager() { return manager; }
    public String getRegion() { return region; }
    public double getAmount() { return amount; }
    @Override public String toString() { return id + ":" + manager + "/" + region + "=" + amount; }
}
