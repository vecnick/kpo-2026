package pos.domain;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public final class WeightedResultService {
    public Optional<BigDecimal> calculate(Course course, String studentId, ScorePolicy policy) {
        Map<String, Evaluation> evaluations = new HashMap<>();
        for (Evaluation evaluation : course.evaluations(studentId)) {
            evaluations.put(evaluation.criterionId(), evaluation);
        }

        BigDecimal totalWeight = BigDecimal.ZERO;
        // Ведём точную сумму дробей и округляем только итоговый процент.
        BigDecimal numerator = BigDecimal.ZERO;
        BigDecimal denominator = BigDecimal.ONE;
        for (Criterion criterion : course.criteria()) {
            Evaluation evaluation = evaluations.get(criterion.id());
            if (!policy.includes(evaluation)) {
                continue;
            }
            totalWeight = totalWeight.add(criterion.weight());
            if (evaluation != null && evaluation.score().value().signum() != 0) {
                BigDecimal points = evaluation.score().value().multiply(criterion.weight());
                numerator = numerator.multiply(criterion.maximum()).add(points.multiply(denominator));
                denominator = denominator.multiply(criterion.maximum());
            }
        }

        if (totalWeight.signum() == 0) {
            return Optional.empty();
        }
        return Optional.of(numerator.movePointRight(2)
                .divide(denominator.multiply(totalWeight), 2, RoundingMode.HALF_UP));
    }
}
