package clients;

/** Ошибка одной строки: номер (с 1, заголовок — строка 1) и причина. */
public final class ImportError {
    private final int lineNumber;
    private final String reason;
    public ImportError(int lineNumber, String reason) { this.lineNumber = lineNumber; this.reason = reason; }
    public int getLineNumber() { return lineNumber; }
    public String getReason() { return reason; }
    @Override public String toString() { return "строка " + lineNumber + ": " + reason; }
}
