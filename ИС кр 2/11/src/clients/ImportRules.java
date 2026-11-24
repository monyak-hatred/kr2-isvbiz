package clients;

/** Числовые правила импорта в одном месте. Вариант 5: дополнительная проверка строки — длина имени <= 30. */
public final class ImportRules {
    private ImportRules() { }
    public static final int FIELDS_COUNT = 3;
    public static final int INN_LEGAL_LENGTH = 10;
    public static final int INN_PERSON_LENGTH = 12;
    /** Вариант 5: имя клиента не длиннее 30 символов (после trim). */
    public static final int MAX_NAME_LENGTH = 30;
}
