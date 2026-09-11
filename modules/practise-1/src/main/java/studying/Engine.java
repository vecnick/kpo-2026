package studying;

public class Engine {

    // final - значит, что после того как двигатель создали, поменять размер педалей уже нельзя
    private final int pedalSize;


    // Конструктор - здесь мы говорим, какой размер педалей будет у этого конкретного двигателя, когда он создаётся
    public Engine(int pedalSize) {
        this.pedalSize = pedalSize;
    }


    // Этот метод просто говорит, как двигатель будет выглядеть, если его напечатать (System.out.println)
    // Без него бы выводилась как-то каша вместо нормального текста
    @Override
    public String toString() {
        return "Двигатель (размер педалей: "+ pedalSize + ")";
    }
}
