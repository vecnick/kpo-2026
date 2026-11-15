package pos.domain;

import java.math.BigDecimal;

public record Score(BigDecimal value) {
    public Score {
        if (value == null || value.signum() < 0) {
            throw new IllegalArgumentException("Балл должен быть неотрицательным");
        }
        value = value.stripTrailingZeros();
    }
}
