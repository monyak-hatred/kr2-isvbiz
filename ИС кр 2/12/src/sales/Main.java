package sales;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/** Читает data/sample.csv («id;менеджер;регион;сумма») и печатает аналитику. Некорректные строки пропускаются с сообщением. */
public class Main {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(args.length > 0 ? args[0] : "data/sample.csv"), StandardCharsets.UTF_8);
        List<Sale> sales = new ArrayList<>();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            try {
                sales.add(parse(line));
            } catch (IllegalArgumentException e) {
                System.out.println("строка " + (i + 1) + ": ОШИБКА: " + e.getMessage());
            }
        }
        SalesAnalyticsService svc = new SalesAnalyticsService();
        System.out.println("Загружено продаж: " + sales.size());
        System.out.println("Топ крупных: " + svc.topSales(sales, 5));
        System.out.println("Максимальная: " + svc.maxSale(sales).map(Sale::toString).orElse("нет продаж"));
        System.out.println("По менеджерам: " + svc.totalsByManager(sales));
        System.out.println("Среднее по региону Север: " + svc.averageByRegion(sales, "Север"));
    }

    static Sale parse(String line) {
        String[] p = line.split(";", -1);
        if (p.length != 4) throw new IllegalArgumentException("ожидалось 4 поля, получено " + p.length);
        try {
            return new Sale(p[0], p[1], p[2], Double.parseDouble(p[3].trim()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("сумма не является числом '" + p[3].trim() + "'");
        }
    }
}
