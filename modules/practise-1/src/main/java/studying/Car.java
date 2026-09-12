package studying;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Car {
    private Engine engine;

    private int vin;

    public Car(int vin, int pedSize) {
        this.vin = vin;
        this.engine = new Engine(pedSize);
    }
}
