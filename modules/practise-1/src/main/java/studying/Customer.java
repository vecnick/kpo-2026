package studying;

import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Customer {
    private Car car;
    private String fio;

    public Customer(Car car, String fio){
        this.car = car;
        this.fio = fio;
    }

    public Customer(String fio){
        this.fio = fio;
    }

    public void setCar(Car car) {
        this.car = car;
    }
}
