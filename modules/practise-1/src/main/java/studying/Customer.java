package studying;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Customer {
    private Car car;
    private String FIO;

    public Customer(String FIO) {
        this.FIO = FIO;
    }
}
