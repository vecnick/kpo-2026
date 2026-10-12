package studying.vyshkat.model;

/** Contract for devices with a beginner-friendliness score. */
public interface BeginnerFriendly {
    /** Minimum score required for a beginner. */
    int BEGINNER_THRESHOLD = 6;

    /** @return beginner-friendliness score from 1 to 10 */
    int beginnerEase();

    /**
     * Determines whether the device may be rented by a beginner.
     *
     * @return {@code true} when the score reaches the threshold
     */
    default boolean isSuitableForBeginner() {
        return beginnerEase() >= BEGINNER_THRESHOLD;
    }
}
