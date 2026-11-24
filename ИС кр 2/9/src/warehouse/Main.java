package warehouse;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/** Команды из data/sample.csv: ITEM;код;название;КАТЕГОРИЯ | RECEIVE;код;кол-во | ISSUE;код;кол-во | FIND;КАТЕГОРИЯ | TOTALS */
public class Main {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(args.length > 0 ? args[0] : "data/sample.csv"), StandardCharsets.UTF_8);
        Warehouse w = new Warehouse();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            System.out.println("строка " + (i + 1) + ": " + execute(w, line));
        }
    }

    static String execute(Warehouse w, String line) {
        String[] p = line.split(";", -1);
        try {
            switch (p[0].trim().toUpperCase()) {
                case "ITEM":
                    need(p, 4); w.register(p[1], p[2], parseCategory(p[3])); return "позиция заведена";
                case "RECEIVE":
                    need(p, 3); w.receive(p[1], Integer.parseInt(p[2].trim())); return "остаток " + w.quantityOf(p[1]);
                case "ISSUE":
                    need(p, 3); w.issue(p[1], Integer.parseInt(p[2].trim())); return "остаток " + w.quantityOf(p[1]);
                case "FIND":
                    need(p, 2); return "найдено: " + w.findByCategory(parseCategory(p[1]));
                case "TOTALS":
                    return w.totalsByCategory().toString();
                default:
                    return "ОШИБКА: неизвестная команда '" + p[0] + "'";
            }
        } catch (NumberFormatException e) {
            return "ОШИБКА: количество должно быть целым числом";
        } catch (IllegalArgumentException | IllegalStateException e) {
            return "ОШИБКА: " + e.getMessage();
        }
    }

    private static Category parseCategory(String s) {
        try {
            return Category.valueOf(s.trim().toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("неизвестная категория '" + s.trim() + "'");
        }
    }

    private static void need(String[] p, int n) {
        if (p.length != n) throw new IllegalArgumentException("команда " + p[0] + " ожидает " + (n - 1) + " аргумент(ов)");
    }
}
