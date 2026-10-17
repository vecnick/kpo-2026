"""Консольный адаптер: повтор ввода при ошибке, чистый выход по EOF/Ctrl+C."""

from collections.abc import Callable
from dataclasses import dataclass

from vyshkat.domain import (
    Asset,
    Bicycle,
    ChargingCabinet,
    DockStation,
    ElectricBicycle,
    Helmet,
    Scooter,
)
from vyshkat.fleet import FleetService


@dataclass(frozen=True)
class AssetOption:
    label: str
    constructor: Callable[..., Asset]
    is_vehicle: bool = False
    uses_energy: bool = False
    has_battery: bool = False


# Только UI знает, какие конкретные типы пользователь может создать из меню.
VEHICLES = {
    "1": AssetOption("Электросамокат", Scooter, True, True, True),
    "2": AssetOption("Велосипед", Bicycle, True),
    "3": AssetOption("Электровелосипед", ElectricBicycle, True, True, True),
}
ITEMS = {
    "1": AssetOption("Шлем", Helmet),
    "2": AssetOption("Док-станция без зарядки", DockStation),
    "3": AssetOption("Зарядный шкаф", ChargingCabinet, uses_energy=True),
}


class ConsoleApp:
    def __init__(
        self,
        fleet: FleetService,
        read: Callable[[str], str] = input,
        write: Callable[[str], None] = print,
    ) -> None:
        self.fleet = fleet
        self.read = read
        self.write = write

    def run(self) -> None:
        self.write("ВышКат — Доедем до дедлайна! Данные хранятся до завершения программы.")
        actions = {
            "1": lambda: self.add_asset(VEHICLES),
            "2": lambda: self.add_asset(ITEMS),
            "3": self.show_report,
            "4": self.show_beginners,
            "5": self.show_inventory,
        }
        try:
            while True:
                self.write(
                    "\n1 — Принять транспорт\n2 — Добавить вещь\n3 — Отчёт\n"
                    "4 — Транспорт для новичков\n5 — Весь баланс\n0 — Выход"
                )
                choice = self.read("Выберите действие: ").strip()
                if choice == "0":
                    break
                action = actions.get(choice)
                if action is None:
                    self.write("Нет такого пункта. Введите число от 0 до 5.")
                    continue
                try:
                    action()
                except ValueError as error:
                    self.write(f"Ошибка: {error} Объект не добавлен; повторите через меню.")
        except (EOFError, KeyboardInterrupt):
            self.write("\nВвод завершён.")
        self.write("До встречи в ВышКат!")

    def _yes_no(self, prompt: str) -> bool:
        while True:
            answer = self.read(prompt + " [д/н]: ").strip().casefold()
            if answer in {"д", "да", "y", "yes"}:
                return True
            if answer in {"н", "нет", "n", "no"}:
                return False
            self.write("Введите д (да) или н (нет).")

    def add_asset(self, options: dict[str, AssetOption]) -> None:
        for key, option in options.items():
            self.write(f"{key} — {option.label}")
        key = self.read("Вид (0 — отмена): ").strip()
        if key == "0":
            self.write("Добавление отменено.")
            return
        if key not in options:
            raise ValueError("Неизвестный вид объекта.")
        option = options[key]
        fields = {
            "inventory_number": self.read("Инвентарный номер (регистр учитывается): "),
            "name": self.read("Наименование: "),
        }
        if option.is_vehicle:
            try:
                fields["beginner_ease"] = int(self.read("Простота для новичка (1–10): "))
            except ValueError:
                raise ValueError(
                    "Простота для новичка должна быть целым числом от 1 до 10."
                ) from None
            fields["brakes_ok"] = self._yes_no("Тормоза исправны?")
        if option.uses_energy:
            try:
                fields["daily_energy_kwh"] = float(
                    self.read("Суточный расход энергии, кВт·ч: ").replace(",", ".")
                )
            except ValueError:
                raise ValueError("Расход энергии должен быть числом, например 2,5.") from None
        if option.has_battery:
            fields["battery_ok"] = self._yes_no("Аккумулятор исправен?")
        self.accept(option.constructor(**fields))

    def accept(self, asset: Asset) -> None:
        result = self.fleet.register(asset)
        status = "ПРИНЯТО" if result.accepted else "ОТКЛОНЕНО"
        self.write(
            f"{status}: {asset.name} [{asset.inventory_number}]. Вид: {asset.kind}. {result.reason}"
        )

    def show_report(self) -> None:
        report = self.fleet.report()
        self.write(
            f"Транспорт: {report.vehicle_count}; вещи: {report.item_count}; "
            f"всего: {report.vehicle_count + report.item_count}.\n"
            f"Энергия транспорта: {report.vehicle_energy_kwh:g} кВт·ч/сутки.\n"
            f"Энергия всего баланса (включая вещи): {report.total_energy_kwh:g} кВт·ч/сутки."
        )

    def show_beginners(self) -> None:
        self.write("Для новичков (простота ≥ 6, только принятый транспорт):")
        vehicles = self.fleet.beginner_vehicles()
        if not vehicles:
            self.write("Подходящего транспорта пока нет.")
        for vehicle in vehicles:
            self.write(
                f"{vehicle.name} | № {vehicle.inventory_number} | "
                f"простота {vehicle.beginner_ease}/10"
            )

    def show_inventory(self) -> None:
        self.write("Баланс: наименование | инвентарный номер")
        assets = self.fleet.inventory()
        if not assets:
            self.write("Баланс пуст.")
        for asset in assets:
            self.write(f"{asset.name} | № {asset.inventory_number}")
