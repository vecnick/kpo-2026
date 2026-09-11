package studying;


import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HseCarFactory {
    private int vinNumber = 0;
    private List<Car> cars = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public boolean createCar(int pedSize) {
        var car = new Car(vinNumber++, pedSize);
        return addCar(car);
    }

    public boolean addCar(Car car) {
        cars.add(car);
        return true;
    }

    public boolean removeCar(Car car) {
        return cars.remove(car);
    }

    public boolean addCustomer(Customer customer) {
        customers.add(customer);
        return true;
    }

    public boolean removeCustomer(Customer customer) {
        return customers.remove(customer);
    }

    public void saleCar() {
        customers.stream()
                .filter(customer -> Objects.isNull(customer.getCar))
                .forEach(customer -> {
                    if (!cars.isEmpty()) {
                        customer.setCar(cars.removeFirst());
                    }
                });
        cars.clear();
    }
}
