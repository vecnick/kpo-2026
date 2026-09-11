package studying;


import lombok.ToString;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@ToString

public class HseCarFactory {
    private int vinNumber = 0;
    private List<Car> cars = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public boolean addCar(int pedSize) {
        cars.add(new Car(++vinNumber, pedSize));
        return true;
    }

    public boolean addCustomer(Customer customer) {
        customers.add(customer);
        return true;
    }

    public void saleCar() {
        customers.stream()
                .filter(customer -> Objects.isNull(customer.getCar()))
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
