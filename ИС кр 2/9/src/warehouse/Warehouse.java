package warehouse;

import java.util.ArrayList;
import java.util.Collections;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;

/**
 * Склад. items — Map по инвентарному коду (поиск и уникальность за O(1), а не перебор списка);
 * movements — List, потому что журнал операций упорядочен по времени и допускает повторы.
 * Внутренние коллекции наружу не отдаются: только копии.
 */
public class Warehouse {
    private final Map<String, StockItem> items = new LinkedHashMap<>();
    private final List<StockMovement> movements = new ArrayList<>();

    /** Заводит новую карточку с нулевым остатком; код должен быть уникален. */
    public void register(String code, String name, Category category) {
        StockItem item = new StockItem(code, name, category);
        if (items.containsKey(item.getCode())) {
            throw new IllegalArgumentException("Позиция с кодом " + item.getCode() + " уже существует");
        }
        items.put(item.getCode(), item);
    }

    public void receive(String code, int quantity) {
        StockItem item = require(code);
        item.increase(quantity);
        movements.add(new StockMovement(item.getCode(), MovementType.RECEIPT, quantity));
    }

    public void issue(String code, int quantity) {
        StockItem item = require(code);
        item.decrease(quantity);
        movements.add(new StockMovement(item.getCode(), MovementType.ISSUE, quantity));
    }

    public int quantityOf(String code) {
        return require(code).getQuantity();
    }

    /** Новый список позиций категории (порядок регистрации); изменение списка не влияет на склад. */
    public List<StockItem> findByCategory(Category category) {
        List<StockItem> result = new ArrayList<>();
        for (StockItem item : items.values()) {
            if (item.getCategory() == category) {
                result.add(item);
            }
        }
        return result;
    }

    /** Суммарный остаток по каждой категории (все категории присутствуют, у пустых — 0). */
    public Map<Category, Integer> totalsByCategory() {
        Map<Category, Integer> totals = new EnumMap<>(Category.class);
        for (Category c : Category.values()) {
            totals.put(c, 0);
        }
        for (StockItem item : items.values()) {
            totals.merge(item.getCategory(), item.getQuantity(), Integer::sum);
        }
        return totals;
    }

    /** Set: уникальный перечень категорий, по которым заведены позиции. */
    public Set<Category> usedCategories() {
        Set<Category> result = EnumSet.noneOf(Category.class);
        for (StockItem item : items.values()) {
            result.add(item.getCategory());
        }
        return result;
    }

    /** Set: отсортированный перечень инвентарных кодов (копия). */
    public Set<String> codes() {
        return new TreeSet<>(items.keySet());
    }

    /** Копия журнала операций. */
    public List<StockMovement> getMovements() {
        return Collections.unmodifiableList(new ArrayList<>(movements));
    }

    private StockItem require(String code) {
        StockItem item = code == null ? null : items.get(code.trim());
        if (item == null) {
            throw new IllegalArgumentException("Позиции с кодом " + code + " нет на складе");
        }
        return item;
    }
}
