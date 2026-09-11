package studying;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public class Car {
    private Engine engine;
    private int vin;

    public Car(int vin){
        this.vin = vin;
        engine = new Engine();
    }
}
