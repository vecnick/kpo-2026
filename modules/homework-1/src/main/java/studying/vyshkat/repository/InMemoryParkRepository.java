package studying.vyshkat.repository;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Repository;
import studying.vyshkat.exception.DuplicateInventoryNumberException;
import studying.vyshkat.model.InventoryItem;

/** Stores fleet inventory in memory while preserving insertion order. */
@Repository
public final class InMemoryParkRepository implements ParkRepository {
    private final Map<String, InventoryItem> items = new LinkedHashMap<>();

    @Override
    public void save(final InventoryItem item) {
        if (existsByInventoryNumber(item.inventoryNumber())) {
            throw new DuplicateInventoryNumberException(
                    item.inventoryNumber());
        }
        items.put(item.inventoryNumber(), item);
    }

    @Override
    public boolean existsByInventoryNumber(final String inventoryNumber) {
        return items.containsKey(inventoryNumber);
    }

    @Override
    public List<InventoryItem> findAll() {
        return List.copyOf(items.values());
    }
}
