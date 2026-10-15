package studying;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class Customer {
    private final String fio;

    @Setter
    private Car car;

    public Customer(String fio) {
        this.fio = fio;
    }
}
