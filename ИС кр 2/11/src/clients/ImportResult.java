package clients;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/** Итог импорта: принято, отклонено, список ошибок (наружу — только чтение). */
public class ImportResult {
    private int accepted;
    private final List<ImportError> errors = new ArrayList<>();

    public void addAccepted() { accepted++; }
    public void addError(int lineNumber, String reason) { errors.add(new ImportError(lineNumber, reason)); }

    public int getAccepted() { return accepted; }
    public int getRejected() { return errors.size(); }
    public List<ImportError> getErrors() { return Collections.unmodifiableList(errors); }

    @Override public String toString() {
        StringBuilder sb = new StringBuilder("Принято: " + accepted + ", отклонено: " + getRejected());
        for (ImportError e : errors) sb.append("\n  ").append(e);
        return sb.toString();
    }
}
