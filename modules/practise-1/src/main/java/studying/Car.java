package studying;

public class Car {
    private final Engine engine;
    private final int VIN;

    public Car(int VIN, int engineSize) {
        this.VIN = VIN;
        engine = new Engine(engineSize);
    }

    public Engine getEngine() {
        return engine;
    }

    public int getVIN() {
        return VIN;
    }

    @Override
    public String toString() {
        return "Car{" + "engine=" + engine + ", VIN=" + VIN + '}';
    }
}
