package studying;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class HseCarFactory {
    private int carNumber = 0;

    private List<Car> cars = new ArrayList<Car>();
    private List<Customer> customers = new ArrayList<Customer>();
    
    public void saleCar() {
        customers.stream()
                .filter(customer -> Objects.isNull(customer.getCar()))
                .forEach(customer -> {
                    if(!cars.isEmpty()) {
                        customer.setCar(cars.removeFirst());
                    }
                });
    }

    public void addCar(int pedSize) {
        cars.add(new Car(carNumber++, pedSize));
    }

    public void addCustomer(String FIO) {
        customers.add(new Customer(FIO));
    }
}
