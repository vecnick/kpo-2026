package studying;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class HseCarFactory {
    private List<Car> cars = new ArrayList<>();
    private List<Customer> customers = new ArrayList<>();

    public boolean addCar(int pedSize) {
        return cars.add(new Car(pedSize));
    }

    public boolean addCustomer(Customer customer) {
        return customers.add(customer);
    }

    public boolean removeCustomer(Customer customer) {
        return customers.remove(customer);
    }

    public void saleCar() {
        Iterator<Car> carIterator = cars.iterator();

        for (Customer customer : customers) {
            if (customer.getCar() == null && carIterator.hasNext()) {
                customer.setCar(carIterator.next());
                carIterator.remove();
            }
        }

        boolean allCustomersHaveCars = customers.stream()
                .allMatch(customer -> customer.getCar() != null);

        if (allCustomersHaveCars) {
            cars.clear();
        }
    }

    public void printCars() {
        cars.forEach(System.out::println);
    }

    public void printCustomers() {
        customers.forEach(System.out::println);
    }
}
