package pos.domain;

import java.math.BigDecimal;

public record Criterion(String id, String name, BigDecimal maximum, BigDecimal weight) {
    public Criterion {
        id = Student.required(id, "ID критерия");
        name = Student.required(name, "Название критерия");
        requirePositive(maximum, "Максимум");
        requirePositive(weight, "Вес");
    }

    private static void requirePositive(BigDecimal value, String field) {
        if (value == null || value.signum() <= 0) {
            throw new IllegalArgumentException(field + " должен быть больше нуля");
        }
    }
}
