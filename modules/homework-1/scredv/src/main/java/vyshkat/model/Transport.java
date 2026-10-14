package vyshkat.model;

/**
 * Общий контракт транспорта, учитываемого в парке.
 */
public interface Transport extends InventoryItem {
    /**
     * Оценка удобства использования транспорта новичком от 1 до 10.
     *
     * @return оценка удобства для новичков от 1 до 10
     */
    int getNoviceFriendliness();
}
