package studying;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Car {
    private Engine engine;

    private int vin;

    public Car(int vin, int pedSize) {
        this.vin = vin;
        this.engine = new Engine(pedSize);
    }
}
