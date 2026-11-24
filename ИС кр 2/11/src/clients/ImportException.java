package clients;

import java.io.IOException;

/** Ошибка уровня файла (нет файла, нет доступа, нет заголовка, ошибка чтения): импорт завершается целиком. */
public class ImportException extends IOException {
    public ImportException(String message, Throwable cause) { super(message, cause); }
    public ImportException(String message) { super(message); }
}
