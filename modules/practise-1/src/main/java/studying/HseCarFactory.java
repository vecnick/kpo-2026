package studying;

import lombok.Getter;

import java.util.ArrayList;


public class HseCarFactory {
    private ArrayList<Customer> customers;
    private ArrayList<Car> cars;

    HseCarFactory(ArrayList<Customer> customers){
        this.customers = customers;
        cars = new ArrayList<>();
    }

    public void saleCar(){
        while (!customers.isEmpty() && !cars.isEmpty()){
            customers.removeFirst().setCar(cars.removeFirst());
            System.out.println("Автомобиль продан");
        }
        if (!cars.isEmpty()){
            cars.clear();
        }
    }

    public void addCar(int vin){
        cars.add(new Car(vin));
    }

    public void addCustomer(Customer customer){
        customers.add(customer);
    }

    public void printCars(){
        for (Car car : cars){
            System.out.println(car);
        }
    }

    public void printCustomers() {
        for (Customer customer : customers){
            System.out.println(customer);
        }
    }
}
