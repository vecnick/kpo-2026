package studying;

public class Customer {
    private final String fullName;
    private Car car;

    public Customer(String fullName) {
        this.fullName = fullName;
    }

    public String getFullName() {
        return fullName;
    }

    public Car getCar() {
        return car;
    }

    public void setCar(Car car) {
        this.car = car;
    }

    @Override
    public String toString() {
        return "Customer{" + "fullName='" + fullName + '\'' + ", car=" + car + '}';
    }
}
