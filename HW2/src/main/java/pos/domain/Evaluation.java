package pos.domain;

public final class Evaluation {
    private final String criterionId;
    private final Score score;

    private Evaluation(String criterionId, Score score) {
        this.criterionId = criterionId;
        this.score = score;
    }

    static Evaluation create(Criterion criterion, Score score) {
        if (score.value().compareTo(criterion.maximum()) > 0) {
            throw new IllegalArgumentException("Балл превышает максимум " + criterion.maximum().toPlainString());
        }
        return new Evaluation(criterion.id(), score);
    }

    public String criterionId() {
        return criterionId;
    }

    public Score score() {
        return score;
    }
}
