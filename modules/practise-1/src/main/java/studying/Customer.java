package studying;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Customer {
    private Car car;
    private String FIO;

    public Customer(String FIO) {
        this.FIO = FIO;
    }
}
