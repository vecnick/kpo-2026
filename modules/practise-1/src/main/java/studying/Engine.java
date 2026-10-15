package studying;

import lombok.Getter;
import lombok.ToString;

@ToString
public class Engine {
    @Getter
    private int pedSize;

    public Engine(int pedSize) {
        this.pedSize = pedSize;
    }
}
