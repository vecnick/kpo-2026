package studying;

import java.util.ArrayList;
import java.util.List;


public class FactoryAF {

    // Склад машин завода. Машины тут появляются только через метод addCar, сам завод их создаёт.
    // Завод владеет машинами  ==> это композиция
    private final List<Car> cars = new ArrayList<>();

    // Очередь клиентов. Сюда добавляются уже готовые объекты Customer, которые были созданы где-то в другом месте (в Main)
    // Завод их не создаёт сам  ==> это агрегация.
    private final List<Customer> customers = new ArrayList<>();


    //  Метод "производит" новую машину и сразу кладёт её на склад
    public void addCar(int serialNumber) {
        cars.add(new Car(serialNumber));
    }

    // Метод ставит уже существующего клиента в очередь
    public void addCustomer(Customer customer) {
        customers.add(customer);
    }

    // Метод продажи машин очереди клиентов
    public void saleCar() {
        for (Customer customer : customers) {
            if (customer.hasCar()) {
                // Если у клиента уже есть машина - его пропускаем, ему второй раз машину давать не надо
                continue;
            }
            if (cars.isEmpty()) {
                // Машины закончились - дальше выдавать нечего
                break;
            }
            // Берём первую машину со склада (она удаляется со склада) и отдаём её клиенту
            Car car = cars.remove(0);
            customer.setCar(car);
        }

        // Если после того как мы прошли всю очередь (все кто хотели машину получили), а на складе всё ещё остались машины,
        // то лишние машины нужно ликвидировать
        if (!cars.isEmpty()) {
            cars.clear();
        }
    }

    // Печатаем список машин на складе
    public void printCars() {
        if (cars.isEmpty()) {
            System.out.println("Склад пуст.");
            return;
        }
        for (Car car : cars) {
            System.out.println(car);
        }
    }

    // Печатаем список клиентов (у кого есть машина, у кого нет)
    public void printCustomers() {
        if (customers.isEmpty()) {
            System.out.println("Очередь пуста.");
            return;
        }
        for (Customer customer : customers) {
            System.out.println(customer);
        }
    }
}