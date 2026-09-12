package studying;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class Car {
    private static int nextVin = 1;

    private Engine engine;
    private int vin;

    public Car(int pedSize) {
        this.engine = new Engine(pedSize);
        this.vin = nextVin++;
    }
}
