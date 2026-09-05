package studying;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString
@RequiredArgsConstructor
public class FactoryAF {
    private int carNumber;
    private List<Car> cars = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    public void addCar(int engineSize) {
        cars.add(new Car(++carNumber, engineSize));
    }

    public void saleCar() {
        customers.stream().filter(customer -> Objects.isNull(customer.getCar()))
                .forEach(customer -> {
                    if (!cars.isEmpty()) {
                        customer.setCar(cars.removeFirst());
                    }
                });
        cars.clear();
    }

    public void printCars() {
        cars.forEach(System.out::println);
    }

    public void printCustomers() {
        customers.forEach(System.out::println);
    }
}
