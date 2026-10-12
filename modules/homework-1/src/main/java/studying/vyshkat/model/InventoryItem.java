package studying.vyshkat.model;

/** Common contract for every item registered on the company balance. */
public interface InventoryItem {
    /** @return human-readable item name */
    String name();

    /** @return unique inventory number */
    String inventoryNumber();
}
