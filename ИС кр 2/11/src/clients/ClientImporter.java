package clients;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Импорт клиентов. Читает построчно через Files.newBufferedReader (файл не загружается в память целиком,
 * ошибка чтения возникает ровно там, где она произошла). Ошибка файла -> ImportException, импорт прерван.
 * Ошибка строки -> запись в ImportResult и переход к следующей строке (try/catch внутри цикла).
 * Дубликат ИНН: побеждает первая запись, повторная отклоняется с указанием номера первой строки.
 */
public class ClientImporter {
    private final ClientCsvParser parser;
    private final ClientValidator validator;
    private final ClientRepository repository;

    public ClientImporter(ClientCsvParser parser, ClientValidator validator, ClientRepository repository) {
        this.parser = parser; this.validator = validator; this.repository = repository;
    }

    public ImportResult importFile(Path path) throws ImportException {
        if (path == null) throw new ImportException("Путь к файлу не задан");
        ImportResult result = new ImportResult();
        try (BufferedReader reader = Files.newBufferedReader(path, StandardCharsets.UTF_8)) {
            String header = reader.readLine();               // строка 1 — заголовок
            if (header == null) {
                throw new ImportException("Файл пуст: нет заголовка");
            }
            java.util.Map<String, Integer> firstSeen = new java.util.HashMap<>();
            int lineNumber = 1;
            String line;
            while ((line = reader.readLine()) != null) {
                lineNumber++;
                try {
                    processLine(line, lineNumber, firstSeen, result);
                } catch (RowFormatException e) {             // ошибка ОДНОЙ строки: фиксируем и продолжаем
                    result.addError(lineNumber, e.getMessage());
                }
            }
        } catch (ImportException e) {
            throw e;
        } catch (IOException e) {                            // нет файла, нет доступа, битая кодировка
            throw new ImportException("Не удалось прочитать файл " + path + ": " + e, e);
        }
        return result;
    }

    private void processLine(String line, int lineNumber, java.util.Map<String, Integer> firstSeen, ImportResult result) {
        ClientDraft draft = parser.parse(line, lineNumber);
        ValidationResult v = validator.validate(draft);
        if (!v.isValid()) {
            result.addError(lineNumber, v.getReason());
            return;
        }
        Integer first = firstSeen.get(draft.getInn());
        if (first != null) {
            result.addError(lineNumber, "дубликат ИНН " + draft.getInn() + " (уже принят в строке " + first + ")");
            return;
        }
        repository.save(new Client(draft.getInn(), draft.getName(), draft.getEmail()));
        firstSeen.put(draft.getInn(), lineNumber);
        result.addAccepted();
    }
}
