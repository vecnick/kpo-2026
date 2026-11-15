package pos.domain;

public record Student(String id, String name) {
    public Student {
        id = required(id, "ID студента");
        name = required(name, "Имя студента");
    }

    static String required(String text, String field) {
        if (text == null || text.isBlank()) {
            throw new IllegalArgumentException(field + " — обязательное поле");
        }
        return text.strip();
    }
}
