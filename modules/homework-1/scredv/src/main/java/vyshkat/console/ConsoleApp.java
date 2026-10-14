package vyshkat.console;

import java.util.List;
import java.util.Scanner;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import vyshkat.exception.ApplicationException;
import vyshkat.model.Bicycle;
import vyshkat.model.ElectricScooter;
import vyshkat.model.Scooter;
import vyshkat.model.Transport;
import vyshkat.model.item.ChargingStation;
import vyshkat.model.item.Helmet;
import vyshkat.model.item.Item;
import vyshkat.service.FleetService;

/**
 * Консольное меню для работы с транспортом и вещами парка.
 */
@RequiredArgsConstructor
public final class ConsoleApp implements CommandLineRunner {
    /** Пункт выбора обычного самоката. */
    private static final int SCOOTER = 1;
    /** Пункт выбора велосипеда. */
    private static final int BICYCLE = 2;
    /** Пункт выбора электросамоката. */
    private static final int ELECTRIC_SCOOTER = 3;
    /** Пункт выбора шлема. */
    private static final int HELMET = 1;
    /** Пункт выбора зарядной станции. */
    private static final int CHARGING_STATION = 2;

    /** Показать суммарное потребление энергии. */
    private static final int SHOW_ENERGY = 3;
    /** Показать транспорт для новичков. */
    private static final int SHOW_NOVICE_TRANSPORT = 4;
    /** Показать весь инвентарь. */
    private static final int SHOW_INVENTORY = 5;

    /** Сервис учёта парка. */
    private final FleetService fleetService;
    /** Считыватель пользовательского ввода. */
    private final Scanner scanner = new Scanner(System.in);

    @Override
    public void run(final String... args) {
        System.out.println("=== ВышКат: система учёта парка ===");
        boolean running = true;
        while (running) {
            printMenu();
            final int choice = readInt("Выберите действие: ");
            switch (choice) {
                case 1 -> addTransport();
                case 2 -> addItem();
                case SHOW_ENERGY -> showTotalEnergy();
                case SHOW_NOVICE_TRANSPORT -> showNoviceTransport();
                case SHOW_INVENTORY -> showAllInventory();
                case 0 -> running = false;
                default -> System.out.println("Неверный пункт меню.");
            }
        }
        System.out.println("Завершение работы.");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("1. Добавить транспорт");
        System.out.println("2. Добавить вещь на баланс");
        System.out.println("3. Показать суммарное потребление энергии");
        System.out.println("4. Показать транспорт для новичков");
        System.out.println("5. Показать весь инвентарь");
        System.out.println("0. Выход");
    }

    private void addTransport() {
        System.out.println("Тип транспорта: 1 - самокат, 2 - велосипед, "
                + "3 - электросамокат");
        final int type = readInt("Тип: ", SCOOTER, ELECTRIC_SCOOTER);
        final String inventoryNumber = readString("Инвентарный номер: ");
        final String modelName = readString("Модель: ");
        final int noviceFriendliness = readInt("Простота для новичка "
                + "(1-10): ", 1, 10);

        final Transport transport = switch (type) {
            case SCOOTER -> Scooter.builder()
                    .inventoryNumber(inventoryNumber)
                    .modelName(modelName)
                    .noviceFriendliness(noviceFriendliness)
                    .build();
            case BICYCLE -> Bicycle.builder()
                    .inventoryNumber(inventoryNumber)
                    .modelName(modelName)
                    .noviceFriendliness(noviceFriendliness)
                    .build();
            case ELECTRIC_SCOOTER -> {
                final double consumption = readDouble(
                        "Суточное потребление (кВт·ч): ");
                yield ElectricScooter.builder()
                        .inventoryNumber(inventoryNumber)
                        .modelName(modelName)
                        .noviceFriendliness(noviceFriendliness)
                        .dailyEnergyConsumption(consumption)
                        .build();
            }
            default -> throw new IllegalStateException(
                    "Недопустимый тип: " + type);
        };

        try {
            fleetService.addTransport(transport);
            System.out.println("Транспорт успешно добавлен.");
        } catch (final ApplicationException e) {
            System.out.println("Отказ [%s]: %s"
                    .formatted(e.getCode(), e.getMessage()));
        }
    }

    private void addItem() {
        System.out.println("Тип вещи: 1 - шлем, 2 - зарядная станция");
        final int type = readInt("Тип: ", HELMET, CHARGING_STATION);
        final String inventoryNumber = readString("Инвентарный номер: ");
        final String name = readString("Наименование: ");

        final Item item = switch (type) {
            case HELMET -> Helmet.builder()
                    .inventoryNumber(inventoryNumber)
                    .name(name)
                    .build();
            case CHARGING_STATION -> {
                final double consumption = readDouble(
                        "Суточное потребление (кВт·ч): ");
                yield ChargingStation.builder()
                        .inventoryNumber(inventoryNumber)
                        .name(name)
                        .dailyEnergyConsumption(consumption)
                        .build();
            }
            default -> throw new IllegalStateException(
                    "Недопустимый тип: " + type);
        };

        try {
            fleetService.addItem(item);
            System.out.println("Вещь добавлена на баланс.");
        } catch (final ApplicationException e) {
            System.out.println("Отказ [%s]: %s"
                    .formatted(e.getCode(), e.getMessage()));
        }
    }

    private void showTotalEnergy() {
        final double total = fleetService.getTotalDailyEnergyConsumption();
        System.out.printf("Суммарное суточное потребление: %.2f "
                + "кВт·ч%n", total);
    }

    private void showNoviceTransport() {
        final List<Transport> transport =
                fleetService.getNoviceFriendlyTransport();
        if (transport.isEmpty()) {
            System.out.println("Нет транспорта, подходящего новичкам.");
            return;
        }
        System.out.println("Транспорт для новичков:");
        transport.forEach(t -> System.out.println("  " + t));
    }

    private void showAllInventory() {
        System.out.println("Инвентарь на балансе:");
        for (final var item : fleetService.getAllInventoryItems()) {
            System.out.println("  " + item);
        }
    }

    private int readInt(final String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(scanner.nextLine().trim());
            } catch (final NumberFormatException e) {
                System.out.println("Ожидалось целое число, попробуйте снова.");
            }
        }
    }

    private int readInt(final String prompt, final int min, final int max) {
        while (true) {
            final int value = readInt(prompt);
            if (value >= min && value <= max) {
                return value;
            }
            System.out.printf("Значение должно быть в диапазоне %d-%d, "
                    + "попробуйте снова.%n",
                    min, max);
        }
    }

    private double readDouble(final String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Double.parseDouble(scanner.nextLine().trim());
            } catch (final NumberFormatException e) {
                System.out.println("Ожидалось число, попробуйте снова.");
            }
        }
    }

    private String readString(final String prompt) {
        System.out.print(prompt);
        return scanner.nextLine().trim();
    }
}
