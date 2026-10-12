package studying.vyshkat.repository;

import java.util.List;
import studying.vyshkat.model.InventoryItem;

/** Storage contract for transport and equipment on the company balance. */
public interface ParkRepository {
    /**
     * Saves an inventory item.
     *
     * @param item item to save
     */
    void save(InventoryItem item);

    /**
     * Checks whether an inventory number is already registered.
     *
     * @param inventoryNumber inventory number to check
     * @return {@code true} when the number exists
     */
    boolean existsByInventoryNumber(String inventoryNumber);

    /** @return immutable snapshot of all registered items */
    List<InventoryItem> findAll();
}
