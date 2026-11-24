package delivery;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

/** Читает data/sample.csv: «способ;км;кг». */
public class Main {
    public static void main(String[] args) throws Exception {
        List<String> lines = Files.readAllLines(Paths.get(args.length > 0 ? args[0] : "data/sample.csv"), StandardCharsets.UTF_8);
        DeliveryService service = new DeliveryService();
        for (int i = 0; i < lines.size(); i++) {
            String line = lines.get(i);
            if (line.trim().isEmpty() || line.startsWith("#")) continue;
            System.out.println("строка " + (i + 1) + ": " + process(service, line));
        }
    }

    static String process(DeliveryService service, String line) {
        String[] p = line.split(";", -1);
        if (p.length != 3) return "ОШИБКА: ожидалось 3 поля, получено " + p.length;
        try {
            DeliveryType type;
            try { type = DeliveryType.valueOf(p[0].trim().toUpperCase()); }
            catch (IllegalArgumentException e) { return "ОШИБКА: неизвестный способ доставки '" + p[0].trim() + "'"; }
            double cost = service.calculate(new DeliveryRequest(type, Double.parseDouble(p[1].trim()), Double.parseDouble(p[2].trim())));
            return String.format("стоимость %.2f", cost);
        } catch (NumberFormatException e) {
            return "ОШИБКА: расстояние и вес должны быть числами";
        } catch (IllegalArgumentException | DeliveryNotPossibleException e) {
            return "ОШИБКА: " + e.getMessage();
        }
    }
}
