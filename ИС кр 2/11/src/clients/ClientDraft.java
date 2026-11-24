package clients;

/** Разобранная, но ещё не проверенная строка. */
public final class ClientDraft {
    private final int lineNumber;
    private final String inn, name, email;

    public ClientDraft(int lineNumber, String inn, String name, String email) {
        this.lineNumber = lineNumber; this.inn = inn; this.name = name; this.email = email;
    }
    public int getLineNumber() { return lineNumber; }
    public String getInn() { return inn; }
    public String getName() { return name; }
    public String getEmail() { return email; }
}
