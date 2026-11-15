package pos.domain;

public enum ScorePolicy {
    FINAL {
        @Override
        boolean includes(Evaluation evaluation) {
            return true;
        }
    },
    GRADED_ONLY {
        @Override
        boolean includes(Evaluation evaluation) {
            return evaluation != null;
        }
    };

    abstract boolean includes(Evaluation evaluation);
}
