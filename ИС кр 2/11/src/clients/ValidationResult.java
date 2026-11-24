package clients;

/** Результат проверки одной строки: ok или причина. */
public final class ValidationResult {
    private final String reason;
    private ValidationResult(String reason) { this.reason = reason; }
    public static ValidationResult ok() { return new ValidationResult(null); }
    public static ValidationResult fail(String reason) { return new ValidationResult(reason); }
    public boolean isValid() { return reason == null; }
    public String getReason() { return reason; }
}
