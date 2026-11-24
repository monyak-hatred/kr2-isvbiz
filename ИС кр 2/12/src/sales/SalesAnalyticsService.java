package sales;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.TreeMap;
import java.util.stream.Collectors;

/** Аналитика по списку продаж. Исходный список не изменяется; внутри stream побочных эффектов нет. */
public class SalesAnalyticsService {

    /** Крупные сделки, отсортированные по сумме (убыв.), затем по региону; не более n. Новый список. */
    public List<Sale> topSales(List<Sale> sales, int n) {
        requireList(sales);
        if (n < 0) throw new IllegalArgumentException("n не может быть отрицательным: " + n);
        return sales.stream()
                .filter(this::isLarge)
                .sorted(SaleComparators.AMOUNT_DESC_THEN_REGION)
                .limit(n)
                .collect(Collectors.toList());
    }

    public List<Sale> topSales(List<Sale> sales) {
        return topSales(sales, SalesRules.DEFAULT_TOP_LIMIT);
    }

    /** Самая крупная продажа; для пустого списка — Optional.empty(). */
    public Optional<Sale> maxSale(List<Sale> sales) {
        requireList(sales);
        return sales.stream().max(java.util.Comparator.comparingDouble(Sale::getAmount));
    }

    /** Суммы по менеджерам: новый Map, отсортированный по имени. */
    public Map<String, Double> totalsByManager(List<Sale> sales) {
        requireList(sales);
        return sales.stream().collect(Collectors.groupingBy(
                Sale::getManager, TreeMap::new, Collectors.summingDouble(Sale::getAmount)));
    }

    /** Средняя сумма продаж региона; если продаж региона нет — 0.0. */
    public double averageByRegion(List<Sale> sales, String region) {
        requireList(sales);
        if (region == null) throw new IllegalArgumentException("Регион не задан");
        OptionalDouble avg = sales.stream()
                .filter(s -> s.getRegion().equals(region.trim()))
                .mapToDouble(Sale::getAmount)
                .average();
        return avg.orElse(0.0);
    }

    // ---- Сравнение императивного и функционального подхода (одна и та же задача) ----

    /** Императивно: сумма крупных сделок. */
    public double largeTotalImperative(List<Sale> sales) {
        requireList(sales);
        double total = 0;
        for (Sale s : sales) {
            if (isLarge(s)) {
                total += s.getAmount();
            }
        }
        return total;
    }

    /** Stream: то же самое одной цепочкой — короче и читается как описание задачи. */
    public double largeTotalStream(List<Sale> sales) {
        requireList(sales);
        return sales.stream().filter(this::isLarge).mapToDouble(Sale::getAmount).sum();
    }

    boolean isLarge(Sale s) {
        return s.getAmount() >= SalesRules.LARGE_SALE_THRESHOLD;
    }

    private static void requireList(List<Sale> sales) {
        if (sales == null) throw new IllegalArgumentException("Список продаж не задан");
    }
}
