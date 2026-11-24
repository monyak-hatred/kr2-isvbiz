package clients;

/** Только разбор: строка -> ClientDraft. Не проверяет содержимое полей. */
public class ClientCsvParser {
    public ClientDraft parse(String line, int lineNumber) {
        if (line == null || line.trim().isEmpty()) {
            throw new RowFormatException("пустая строка");
        }
        String[] p = line.split(";", -1);     // -1: хвостовые пустые поля сохраняются
        if (p.length != ImportRules.FIELDS_COUNT) {
            throw new RowFormatException("ожидалось " + ImportRules.FIELDS_COUNT + " поля(ей), получено " + p.length);
        }
        return new ClientDraft(lineNumber, p[0].trim(), p[1].trim(), p[2].trim());
    }
}
