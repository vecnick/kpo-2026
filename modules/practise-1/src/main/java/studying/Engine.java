package studying;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class Engine {
    private int pedSIze;

    @Override
    public String toString() {
        return "Engine{" + "size=" + pedSIze + '}';
    }
}
