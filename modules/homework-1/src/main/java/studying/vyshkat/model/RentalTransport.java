package studying.vyshkat.model;

/** Common contract for transport available for rent. */
public interface RentalTransport extends InventoryItem, BeginnerFriendly {
    /** @return technical condition score from 1 to 10 */
    int technicalCondition();
}
