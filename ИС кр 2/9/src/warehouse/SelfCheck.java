package warehouse;

import java.util.List;
import java.util.Map;

/** Самопроверка. Запуск: java -cp out warehouse.SelfCheck */
public class SelfCheck {
    private static int passed, failed;

    public static void main(String[] args) {
        Warehouse w = new Warehouse();
        w.register("PC-1", "Ноутбук", Category.COMPUTERS);
        w.register("PC-2", "Моноблок", Category.COMPUTERS);
        w.register("MS-1", "Мышь", Category.PERIPHERALS);
        w.register("SW-1", "Коммутатор", Category.NETWORK);

        bad("дубликат кода", () -> w.register("PC-1", "Другое", Category.FURNITURE));
        bad("пустой код", () -> w.register(" ", "X", Category.FURNITURE));
        bad("null категория", () -> w.register("Z", "X", null));

        w.receive("PC-1", 5); w.receive("PC-2", 3); w.receive("MS-1", 10);
        check("приход увеличивает остаток", w.quantityOf("PC-1") == 5);
        w.issue("PC-1", 2);
        check("выдача уменьшает остаток", w.quantityOf("PC-1") == 3);
        w.issue("PC-1", 3);
        check("выдача ровно всего остатка допустима (0)", w.quantityOf("PC-1") == 0);
        badState("выдача больше остатка (0 -> -1)", () -> w.issue("PC-1", 1));
        check("остаток не стал отрицательным", w.quantityOf("PC-1") == 0);
        bad("выдача 0", () -> w.issue("MS-1", 0));
        bad("выдача отрицательного", () -> w.issue("MS-1", -1));
        bad("приход 0", () -> w.receive("MS-1", 0));
        bad("приход отрицательного", () -> w.receive("MS-1", -5));
        bad("неизвестный код", () -> w.receive("NO", 1));
        bad("null код", () -> w.issue(null, 1));

        // Вариант 5: категория NETWORK, лимит 30
        w.receive("SW-1", 30);
        check("NETWORK: ровно 30 допустимо (верхняя граница)", w.quantityOf("SW-1") == 30);
        badState("NETWORK: 30 + 1 превышает лимит", () -> w.receive("SW-1", 1));
        check("после отказа остаток не изменился", w.quantityOf("SW-1") == 30);
        w.issue("SW-1", 1);
        w.receive("SW-1", 1);
        check("NETWORK: освободили место и снова 30", w.quantityOf("SW-1") == 30);
        Warehouse w2 = new Warehouse();
        w2.register("SW-9", "Роутер", Category.NETWORK);
        badState("NETWORK: разовый приход 31", () -> w2.receive("SW-9", 31));
        w2.register("PC-9", "ПК", Category.COMPUTERS);
        w2.receive("PC-9", 1000);
        check("другие категории без лимита (1000)", w2.quantityOf("PC-9") == 1000);
        badState("переполнение int при приходе", () -> w2.receive("PC-9", Integer.MAX_VALUE));

        // Поиск возвращает копию
        List<StockItem> found = w.findByCategory(Category.COMPUTERS);
        check("findByCategory: 2 позиции", found.size() == 2);
        found.clear();
        check("очистка результата не влияет на склад", w.findByCategory(Category.COMPUTERS).size() == 2);
        check("категория без позиций -> пустой список", w.findByCategory(Category.FURNITURE).isEmpty());

        Map<Category, Integer> totals = w.totalsByCategory();
        check("итоги COMPUTERS = 3 (PC-1: 0, PC-2: 3)", totals.get(Category.COMPUTERS) == 3);
        check("итоги PERIPHERALS = 10", totals.get(Category.PERIPHERALS) == 10);
        check("итоги FURNITURE = 0", totals.get(Category.FURNITURE) == 0);
        check("итоги NETWORK = 30", totals.get(Category.NETWORK) == 30);
        totals.put(Category.COMPUTERS, 999);
        check("изменение сводки не влияет на склад", w.totalsByCategory().get(Category.COMPUTERS) == 3);

        check("Set категорий: 3 уникальные", w.usedCategories().size() == 3);
        check("Set кодов отсортирован", w.codes().toString().equals("[MS-1, PC-1, PC-2, SW-1]"));
        w.codes().clear();
        check("изменение Set кодов не влияет на склад", w.codes().size() == 4);

        check("журнал: 8 операций (6 приходов/выдач до лимита + 2 после)", w.getMovements().size() == 8);
        bad("журнал только для чтения", () -> w.getMovements().clear());
        check("equals по коду", new StockItem("A", "x", Category.FURNITURE).equals(new StockItem("A", "y", Category.COMPUTERS)));

        System.out.println("Пройдено: " + passed + ", провалено: " + failed);
        if (failed > 0) System.exit(1);
    }

    private static void bad(String name, Runnable r) {
        try { r.run(); check(name + " (исключение не выброшено)", false); }
        catch (IllegalArgumentException | UnsupportedOperationException e) { check(name + " -> " + e.getMessage(), true); }
        catch (IllegalStateException e) { check(name + " (ожидалось IllegalArgument, получено State: " + e.getMessage() + ")", false); }
    }
    private static void badState(String name, Runnable r) {
        try { r.run(); check(name + " (исключение не выброшено)", false); }
        catch (IllegalStateException e) { check(name + " -> " + e.getMessage(), true); }
    }
    private static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "OK   " : "FAIL ") + name);
    }
}
