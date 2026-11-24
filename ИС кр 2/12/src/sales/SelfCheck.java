package sales;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Optional;

/** Самопроверка. Запуск: java -cp out sales.SelfCheck */
public class SelfCheck {
    private static int passed, failed;
    private static final SalesAnalyticsService S = new SalesAnalyticsService();

    private static Sale s(String id, String m, String r, double a) { return new Sale(id, m, r, a); }

    public static void main(String[] args) {
        List<Sale> sales = new ArrayList<>(Arrays.asList(
                s("1", "Иванов", "Юг", 150_000), s("2", "Петров", "Север", 150_000), s("3", "Иванов", "Север", 99_999.99),
                s("4", "Сидоров", "Запад", 200_000), s("5", "Петров", "Юг", 100_000), s("6", "Иванов", "Восток", 50_000)));
        List<Sale> snapshot = new ArrayList<>(sales);

        // topSales: фильтр, сортировка по двум полям, limit
        List<Sale> top = S.topSales(sales, 10);
        check("topSales: только крупные (>= 100000), 4 штуки", top.size() == 4);
        check("порог: ровно 100000 включён", containsId(top, "5"));
        check("порог: 99999.99 исключён", !containsId(top, "3"));
        check("первичная сортировка: 200000 первая", top.get(0).getId().equals("4"));
        check("вторичная (регион): при 150000 Север раньше Юг", top.get(1).getId().equals("2") && top.get(2).getId().equals("1"));
        check("последняя — 100000", top.get(3).getId().equals("5"));
        check("limit 2", S.topSales(sales, 2).size() == 2);
        check("limit 0 -> пусто", S.topSales(sales, 0).isEmpty());
        check("limit больше размера -> все крупные", S.topSales(sales, 1000).size() == 4);
        check("topSales без n использует DEFAULT_TOP_LIMIT (30)", S.topSales(sales).size() == 4 && SalesRules.DEFAULT_TOP_LIMIT == 30);
        bad("limit отрицательный", () -> S.topSales(sales, -1));
        bad("topSales(null)", () -> S.topSales(null, 1));
        top.clear();
        check("изменение результата не влияет на источник", sales.size() == 6);

        // maxSale / Optional
        Optional<Sale> max = S.maxSale(sales);
        check("maxSale присутствует: 200000", max.isPresent() && max.get().getId().equals("4"));
        check("maxSale пустого списка -> Optional.empty()", !S.maxSale(Collections.emptyList()).isPresent());
        bad("maxSale(null)", () -> S.maxSale(null));

        // totalsByManager
        Map<String, Double> totals = S.totalsByManager(sales);
        check("Иванов = 150000 + 99999.99 + 50000", Math.abs(totals.get("Иванов") - 299_999.99) < 1e-6);
        check("Петров = 250000", totals.get("Петров") == 250_000.0);
        check("Сидоров = 200000", totals.get("Сидоров") == 200_000.0);
        check("менеджеры отсортированы", totals.keySet().toString().equals("[Иванов, Петров, Сидоров]"));
        check("пустой список -> пустой Map", S.totalsByManager(Collections.emptyList()).isEmpty());
        totals.put("Новый", 1.0);
        check("изменение Map не влияет на следующий расчёт", S.totalsByManager(sales).size() == 3);

        // averageByRegion
        check("среднее Север = (150000 + 99999.99)/2", Math.abs(S.averageByRegion(sales, "Север") - 124_999.995) < 1e-6);
        check("среднее региона без продаж = 0", S.averageByRegion(sales, "Мордор") == 0.0);
        bad("регион null", () -> S.averageByRegion(sales, null));

        // Императивно vs stream дают одно и то же
        check("largeTotalImperative == largeTotalStream", S.largeTotalImperative(sales) == S.largeTotalStream(sales));
        check("сумма крупных = 600000", S.largeTotalStream(sales) == 600_000.0);

        // Исходная коллекция не изменялась
        check("исходный список не изменился (содержимое и порядок)", sales.equals(snapshot));

        // Некорректные данные
        bad("Sale: отрицательная сумма", () -> s("x", "m", "r", -1));
        bad("Sale: NaN", () -> s("x", "m", "r", Double.NaN));
        bad("Sale: пустой менеджер", () -> s("x", " ", "r", 1));
        bad("Main.parse: 3 поля", () -> Main.parse("1;a;b"));
        bad("Main.parse: не число", () -> Main.parse("1;a;b;abc"));
        check("Main.parse: корректная", Main.parse("1;a;b;5").getAmount() == 5.0);

        System.out.println("Пройдено: " + passed + ", провалено: " + failed);
        if (failed > 0) System.exit(1);
    }

    private static boolean containsId(List<Sale> l, String id) {
        for (Sale x : l) if (x.getId().equals(id)) return true;
        return false;
    }
    private static void bad(String name, Runnable r) {
        try { r.run(); check(name + " (исключение не выброшено)", false); }
        catch (IllegalArgumentException e) { check(name + " -> " + e.getMessage(), true); }
    }
    private static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "OK   " : "FAIL ") + name);
    }
}
