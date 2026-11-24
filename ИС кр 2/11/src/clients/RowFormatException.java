package clients;

/** Ошибка структуры одной строки (непроверяемая; локализуется в цикле импорта по строке). */
public class RowFormatException extends RuntimeException {
    public RowFormatException(String message) { super(message); }
}
