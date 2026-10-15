package studying;


public class Car {

    // Это размер педалей, который будет у всех машин завода одинаковый
    private static final int DEFAULT_PEDAL_SIZE = 77;

    // Порядковый номер машины - просто чтобы отличать машины друг от друга.
    // final - потому что номер машины после выпуска не меняется.
    private final int serialNumber;

    // Двигатель - часть автомобиля, которая создаётся вместе с ним (композиция)
    private final Engine engine;



    public Car(int serialNumber) {
        this.serialNumber = serialNumber;
        // Engine создаётся здесь же, внутри Car - это и есть композиция. Engine не может жить без Car
        this.engine = new Engine(DEFAULT_PEDAL_SIZE);
    }

    public int getSerialNumber() {
        return serialNumber;
    }

    public Engine getEngine() {
        return engine;
    }

    // Красивый вывод машины в консоль
    @Override
    public String toString() {
        return "Автомобиль №" + serialNumber + " с [" + engine + "]";
    }
}
