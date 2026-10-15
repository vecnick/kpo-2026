package studying;

import lombok.RequiredArgsConstructor;
import lombok.ToString;

import java.util.ArrayList;

@ToString
@RequiredArgsConstructor
public class HseCarFactory {
    private int numberOfCar = 1;
    private ArrayList<Car> cars = new ArrayList<Car>();
    private ArrayList<Customer> customers = new ArrayList<Customer>();

    public boolean addCar(int pedSize) {
        Car car = new Car(numberOfCar++, pedSize);
        return cars.add(car);
    }

    public boolean addCustomer(Customer customer) {
        return customers.add(customer);
    }

    public void printCars() {
        for (Car car : cars) {
            System.out.println(car);
        }
    }

    public void printCustomers() {
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }

    public void saleCar() {
        for (Customer customer : customers) {
            if (!cars.isEmpty() && customer.getCar() == null) {
                customer.setCar(cars.get(0));
                cars.remove(0);
            }
        }
        cars.clear();
    }

}
