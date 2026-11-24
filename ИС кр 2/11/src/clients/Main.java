package clients;

import java.nio.file.Paths;

/** Импортирует data/sample.csv (или файл из аргумента) и печатает отчёт. */
public class Main {
    public static void main(String[] args) {
        String path = args.length > 0 ? args[0] : "data/sample.csv";
        ClientImporter importer = new ClientImporter(new ClientCsvParser(), new ClientValidator(), new ClientRepository());
        try {
            System.out.println(importer.importFile(Paths.get(path)));
        } catch (ImportException e) {
            System.out.println("ИМПОРТ ПРЕРВАН: " + e.getMessage());
        }
    }
}
