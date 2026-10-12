package studying.vyshkat.exception;

/** Raised when an inventory number is already registered. */
public final class DuplicateInventoryNumberException
        extends IllegalArgumentException {
    /**
     * Creates an exception for a duplicated inventory number.
     *
     * @param inventoryNumber duplicated inventory number
     */
    public DuplicateInventoryNumberException(final String inventoryNumber) {
        super("Инвентарный номер уже зарегистрирован: " + inventoryNumber);
    }
}
