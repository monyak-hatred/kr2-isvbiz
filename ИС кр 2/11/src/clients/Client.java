package clients;

/** Принятая карточка клиента. */
public final class Client {
    private final String inn;
    private final String name;
    private final String email;

    public Client(String inn, String name, String email) {
        this.inn = inn; this.name = name; this.email = email;
    }
    public String getInn() { return inn; }
    public String getName() { return name; }
    public String getEmail() { return email; }
    @Override public String toString() { return inn + " " + name + " <" + email + ">"; }
}
