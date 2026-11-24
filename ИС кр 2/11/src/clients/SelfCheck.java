package clients;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/** Самопроверка на временных файлах. Запуск: java -cp out clients.SelfCheck */
public class SelfCheck {
    private static int passed, failed;
    private static final String H = "inn;name;email\n";

    public static void main(String[] args) throws Exception {
        ClientRepository repo = new ClientRepository();
        ImportResult r = run(repo, H + "7707083893;ООО Ромашка;info@romashka.ru\n123456789012;Иванов Иван;ivan@mail.ru\n");
        check("типовой файл: 2 принято, 0 отклонено", r.getAccepted() == 2 && r.getRejected() == 0);
        check("клиент сохранён в репозиторий", repo.findByInn("7707083893") != null && repo.size() == 2);

        // ИНН
        rejected("ИНН 9 цифр", "123456789;Имя;a@b.c", 2, "10 или 12");
        accepted("ИНН 10 цифр", "1234567890;Имя;a@b.c");
        rejected("ИНН 11 цифр", "12345678901;Имя;a@b.c", 2, "10 или 12");
        accepted("ИНН 12 цифр", "123456789012;Имя;a@b.c");
        rejected("ИНН 13 цифр", "1234567890123;Имя;a@b.c", 2, "10 или 12");
        rejected("ИНН с буквой", "12345678a0;Имя;a@b.c", 2, "только из цифр");
        rejected("ИНН пустой", ";Имя;a@b.c", 2, "ИНН не задан");
        // Email
        rejected("email без @", "1234567890;Имя;abc.ru", 2, "не содержит @");
        rejected("email с двумя @", "1234567890;Имя;a@b@c", 2, "более одного");
        rejected("email пустая левая часть", "1234567890;Имя;@b.c", 2, "слева");
        rejected("email пустая правая часть", "1234567890;Имя;a@", 2, "справа");
        rejected("email пустой", "1234567890;Имя;", 2, "не содержит @");
        // Вариант 5: имя <= 30
        accepted("имя ровно 30 символов", "1234567890;" + "x".repeat(30) + ";a@b.c");
        rejected("имя 31 символ", "1234567890;" + "x".repeat(31) + ";a@b.c", 2, "длиннее 30");
        rejected("имя пустое", "1234567890;;a@b.c", 2, "имя не задано");
        // Структура строки
        rejected("пустая строка", "", 2, "пустая строка");
        rejected("два поля", "1234567890;Имя", 2, "получено 2");
        rejected("четыре поля", "1234567890;Имя;a@b.c;x", 2, "получено 4");
        // Продолжение после ошибки и номера строк
        r = run(new ClientRepository(), H + "bad\n1234567890;Имя;a@b.c\n12;Имя;a@b.c\n123456789012;Имя2;b@c.d\n");
        check("импорт продолжается после ошибок: принято 2", r.getAccepted() == 2);
        check("отклонено 2 (строки 2 и 4)", r.getRejected() == 2 && r.getErrors().get(0).getLineNumber() == 2 && r.getErrors().get(1).getLineNumber() == 4);
        // Дубликат ИНН
        r = run(new ClientRepository(), H + "1234567890;Первый;a@b.c\n1234567890;Второй;x@y.z\n");
        check("дубликат ИНН: принят первый, второй отклонён", r.getAccepted() == 1 && r.getRejected() == 1);
        check("сообщение указывает строку первой записи", r.getErrors().get(0).getReason().contains("строке 2"));
        ClientRepository rp = new ClientRepository();
        run(rp, H + "1234567890;Первый;a@b.c\n1234567890;Второй;x@y.z\n");
        check("в репозитории осталась первая запись", rp.findByInn("1234567890").getName().equals("Первый"));
        // Отклонённая запись не мешает последующему дубликату: невалидная строка не занимает ИНН
        r = run(new ClientRepository(), H + "1234567890;Имя;нет-собаки\n1234567890;Имя;a@b.c\n");
        check("невалидная строка не занимает ИНН", r.getAccepted() == 1 && r.getRejected() == 1);
        // Только заголовок
        r = run(new ClientRepository(), "inn;name;email\n");
        check("только заголовок: 0/0", r.getAccepted() == 0 && r.getRejected() == 0);
        // Ошибки файла прерывают импорт
        ClientImporter imp = new ClientImporter(new ClientCsvParser(), new ClientValidator(), new ClientRepository());
        try { imp.importFile(Path.of("/no/such/file.csv")); check("нет файла (исключение не выброшено)", false); }
        catch (ImportException e) { check("нет файла -> ImportException: " + e.getMessage(), true); }
        Path empty = Files.createTempFile("clients", ".csv");
        try { imp.importFile(empty); check("пустой файл (исключение не выброшено)", false); }
        catch (ImportException e) { check("пустой файл -> " + e.getMessage(), true); }
        Path bad = Files.createTempFile("clients", ".csv");
        Files.write(bad, new byte[]{'h', '\n', (byte) 0xC3, (byte) 0x28, '\n'});   // некорректный UTF-8
        try { imp.importFile(bad); check("битая кодировка (исключение не выброшено)", false); }
        catch (ImportException e) { check("битая кодировка -> ImportException", true); }
        try { imp.importFile(null); check("null путь (исключение не выброшено)", false); }
        catch (ImportException e) { check("null путь -> " + e.getMessage(), true); }
        try { r.getErrors().clear(); check("список ошибок только для чтения (не выброшено)", false); }
        catch (UnsupportedOperationException e) { check("список ошибок только для чтения", true); }

        System.out.println("Пройдено: " + passed + ", провалено: " + failed);
        if (failed > 0) System.exit(1);
    }

    private static ImportResult run(ClientRepository repo, String content) throws IOException {
        Path f = Files.createTempFile("clients", ".csv");
        Files.write(f, content.getBytes(StandardCharsets.UTF_8));
        try {
            return new ClientImporter(new ClientCsvParser(), new ClientValidator(), repo).importFile(f);
        } finally {
            Files.deleteIfExists(f);
        }
    }
    private static void accepted(String name, String row) throws IOException {
        ImportResult r = run(new ClientRepository(), H + row + "\n");
        check(name + " -> принято", r.getAccepted() == 1 && r.getRejected() == 0);
    }
    private static void rejected(String name, String row, int line, String reason) throws IOException {
        ImportResult r = run(new ClientRepository(), H + row + "\n");
        boolean ok = r.getAccepted() == 0 && r.getRejected() == 1
                && r.getErrors().get(0).getLineNumber() == line && r.getErrors().get(0).getReason().contains(reason);
        check(name + " -> " + (r.getRejected() == 1 ? r.getErrors().get(0) : "не отклонено"), ok);
    }
    private static void check(String name, boolean ok) {
        if (ok) passed++; else failed++;
        System.out.println((ok ? "OK   " : "FAIL ") + name);
    }
}
