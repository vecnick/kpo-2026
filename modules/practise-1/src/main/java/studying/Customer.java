package studying;


public class Customer {

    // ФИО покупателя
    private final String fullName;

    // Автомобиль, который достался покупателю.
    // Customer просто хранит ссылку на Car. Не final, потому что изначально машины нет (null), а потом она может появиться
    // Это агрегация: Customer не создаёт машину сам, он просто получает её потом, когда её ему передадут


    private Car car;

    public Customer(String fullName) {
        this.fullName = fullName;
        this.car = null;    // Пока машины нет
    }


    public String getFullName() {
        return fullName;
    }

    public Car getCar() {
        return car;
    }

    // Через этот метод FactoryAF потом "вручает" клиенту машину
    public void setCar(Car car) {
        this.car = car;
    }

    // Метод, чтобы не писать каждый раз "car != null", а просто спросить "есть ли у него машина"
    public boolean hasCar() {
        return car != null;
    }


    @Override
    public String toString() {
        return fullName + " ==> " + (hasCar() ? car.toString() : "ещё ждёт свою машину");
    }
}

