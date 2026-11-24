package delivery;

import static delivery.DeliveryType.*;

/** Самопроверка. Запуск: java -cp out delivery.SelfCheck */
public class SelfCheck {
    private static int passed, failed;
    private static final DeliveryService S = new DeliveryService();

    public static void main(String[] args) {
        // Формулы
        cost("самовывоз 0 км 5 кг = 0", PICKUP, 0, 5, 0.0);
        cost("самовывоз ровно 50 кг (верхняя граница)", PICKUP, 100, 50, 0.0);
        cost("стандарт 10 км 2 кг = 10+30+16 = 56", STANDARD, 10, 2, 56.0);
        cost("экспресс 10 км 2 кг = 40+80+24 = 144", EXPRESS, 10, 2, 144.0);
        cost("постамат 10 км 2 кг = 5+20+8 = 33", LOCKER, 10, 2, 33.0);
        // Минимальная стоимость 30 (вариант 5)
        cost("постамат 0 км 1 кг: 9 -> минимум 30", LOCKER, 0, 1, 30.0);
        cost("постамат 3 км 4 кг: 5+6+16 = 27 -> 30", LOCKER, 3, 4, 30.0);
        cost("постамат 4 км 4 кг: 5+8+16 = 29 -> 30", LOCKER, 4, 4, 30.0);
        cost("постамат 5 км 4 кг: 5+10+16 = 31 (выше минимума)", LOCKER, 5, 4, 31.0);
        cost("стандарт 0 км 1 кг: 18 -> минимум 30", STANDARD, 0, 1, 30.0);
        cost("стандарт 0 км 2.5 кг: 10+20 = 30 ровно", STANDARD, 0, 2.5, 30.0);
        cost("самовывоз не подпадает под минимум", PICKUP, 0, 1, 0.0);
        // Ограничения
        cost("стандарт ровно 30 кг и 500 км допустим", STANDARD, 500, 30, 10 + 1500 + 240.0);
        impossible("стандарт 30.01 кг", STANDARD, 1, 30.01);
        impossible("стандарт 500.5 км", STANDARD, 500.5, 1);
        cost("экспресс ровно 20 кг и 100 км допустим", EXPRESS, 100, 20, 40 + 800 + 240.0);
        impossible("экспресс 20.1 кг", EXPRESS, 1, 20.1);
        impossible("экспресс 101 км", EXPRESS, 101, 1);
        cost("постамат ровно 10 кг и 50 км допустим", LOCKER, 50, 10, 5 + 100 + 40.0);
        impossible("постамат 10.1 кг", LOCKER, 1, 10.1);
        impossible("постамат 51 км", LOCKER, 51, 1);
        impossible("самовывоз 50.1 кг", PICKUP, 0, 50.1);
        // Некорректные данные
        bad("null тип", () -> new DeliveryRequest(null, 1, 1));
        bad("отрицательное расстояние", () -> new DeliveryRequest(STANDARD, -1, 1));
        bad("вес 0", () -> new DeliveryRequest(STANDARD, 1, 0));
        bad("вес NaN", () -> new DeliveryRequest(STANDARD, 1, Double.NaN));
        bad("null запрос", () -> S.calculate(null));
        // Структура: фабрика выдаёт нужные реализации, полиморфизм
        DeliveryPolicyFactory f = new DeliveryPolicyFactory();
        check("фабрика: PICKUP -> PickupPolicy", f.get(PICKUP) instanceof PickupPolicy);
        check("фабрика: LOCKER -> LockerPolicy", f.get(LOCKER) instanceof LockerPolicy);
        check("фабрика: для всех типов enum есть реализация", allTypesCovered(f));
        // Расширяемость: новая политика без изменения существующих
        DeliveryPolicy free = new DeliveryPolicy() {
            public boolean supports(DeliveryRequest r) { return true; }
            public double calculateCost(DeliveryRequest r) { return 1.0; }
            public String describeLimits() { return "без ограничений"; }
        };
        DeliveryRequest req = new DeliveryRequest(STANDARD, 1, 1);
        check("новая реализация интерфейса работает независимо", free.supports(req) && free.calculateCost(req) == 1.0);
        // Парсинг Main
        m("Main: корректная", "standard;10;2", "стоимость 56.00");
        m("Main: пустая", "", "ОШИБКА");
        m("Main: неизвестный способ", "drone;1;1", "неизвестный способ");
        m("Main: не число", "standard;abc;1", "ОШИБКА");
        m("Main: слишком тяжёлый", "express;1;25", "невозможна");

        System.out.println("Пройдено: " + passed + ", провалено: " + failed);
        if (failed > 0) System.exit(1);
    }

    private static boolean allTypesCovered(DeliveryPolicyFactory f) {
        for (DeliveryType t : DeliveryType.values()) f.get(t);
        return true;
    }
    private static void cost(String name, DeliveryType t, double km, double kg, double expected) {
        try { double got = S.calculate(new DeliveryRequest(t, km, kg)); check(name + " -> " + got, Math.abs(got - expected) < 1e-9); }
        catch (RuntimeException e) { check(name + " (неожиданное исключение: " + e.getMessage() + ")", false); }
    }
    private static void impossible(String name, DeliveryType t, double km, double kg) {
        try { S.calculate(new DeliveryRequest(t, km, kg)); check(name + " (исключение не выброшено)", false); }
        catch (DeliveryNotPossibleException e) { check(name + " -> " + e.getMessage(), true); }
    }
    private static void bad(String name, Runnable r) {
        try { r.run(); check(name + " (исключение не выброшено)", false); }
        catch (IllegalArgumentException e) { check(name + " -> " + e.getMessage(), true); }
    }
    private static void m(String name, String line, String contains) {
        String res = Main.process(S, line);
        check(name + " -> " + res, res.contains(contains));
    }
    private static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "OK   " : "FAIL ") + name);
    }
}
