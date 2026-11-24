package clients;

import java.util.LinkedHashMap;
import java.util.Map;

/** Хранилище клиентов в памяти; ключ — ИНН. */
public class ClientRepository {
    private final Map<String, Client> byInn = new LinkedHashMap<>();

    public void save(Client c) { byInn.put(c.getInn(), c); }
    public Client findByInn(String inn) { return byInn.get(inn); }
    public int size() { return byInn.size(); }
}
