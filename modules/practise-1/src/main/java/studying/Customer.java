package studying;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Customer {
    private Car car;
    private String FIO;

    public Customer (String FIO) {
        this.FIO = FIO;
    }
}
