package studying;

import lombok.Getter;
import lombok.ToString;


@ToString
public class Car {
    @Getter
    private int number;
    private Engine engine;

    public Car(int number, int pedSize) {
        this.engine = new Engine(pedSize);
        this.number = number;
    }
}
