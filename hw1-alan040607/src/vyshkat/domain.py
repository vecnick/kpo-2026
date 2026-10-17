"""Транспорт, вещи и общие правила проверки их данных."""

from abc import ABC, abstractmethod
from dataclasses import dataclass
from math import isfinite
from typing import Protocol, runtime_checkable


@runtime_checkable
class Inventoriable(Protocol):
    """Инвентаризация не предполагает ни движение, ни потребление энергии."""

    @property
    def inventory_number(self) -> str: ...

    @property
    def name(self) -> str: ...


@runtime_checkable
class EnergyConsumer(Protocol):
    @property
    def daily_energy_kwh(self) -> float: ...


def validate_energy(value: float) -> None:
    if isinstance(value, bool) or not isinstance(value, (int, float)):
        raise ValueError("Расход энергии должен быть числом.")
    try:
        valid = isfinite(value) and value >= 0
    except OverflowError:
        valid = False
    if not valid:
        raise ValueError("Расход энергии должен быть конечным неотрицательным числом.")


@dataclass(frozen=True, kw_only=True)
class Asset(ABC):
    inventory_number: str
    name: str

    def __post_init__(self) -> None:
        for value in (self.inventory_number, self.name):
            if not isinstance(value, str) or not value.strip():
                raise ValueError("Наименование и инвентарный номер не могут быть пустыми.")

        # В frozen dataclass обычное присваивание запрещено, даже при инициализации.
        object.__setattr__(self, "inventory_number", self.inventory_number.strip())
        object.__setattr__(self, "name", self.name.strip())

    @property
    @abstractmethod
    def kind(self) -> str:
        """Человекочитаемый вид объекта для консоли."""


@dataclass(frozen=True, kw_only=True)
class Transport(Asset, ABC):
    beginner_ease: int
    brakes_ok: bool = True

    def __post_init__(self) -> None:
        super().__post_init__()
        if type(self.beginner_ease) is not int or not 1 <= self.beginner_ease <= 10:
            raise ValueError("Простота для новичка должна быть целым числом от 1 до 10.")
        if type(self.brakes_ok) is not bool:
            raise ValueError("Состояние тормозов должно быть логическим значением.")


@dataclass(frozen=True, kw_only=True)
class ElectricTransport(Transport, ABC):
    daily_energy_kwh: float
    battery_ok: bool = True

    def __post_init__(self) -> None:
        super().__post_init__()
        validate_energy(self.daily_energy_kwh)
        if type(self.battery_ok) is not bool:
            raise ValueError("Состояние аккумулятора должно быть логическим значением.")


@dataclass(frozen=True, kw_only=True)
class Scooter(ElectricTransport):
    @property
    def kind(self) -> str:
        return "Электросамокат"


@dataclass(frozen=True, kw_only=True)
class ElectricBicycle(ElectricTransport):
    @property
    def kind(self) -> str:
        return "Электровелосипед"


@dataclass(frozen=True, kw_only=True)
class Bicycle(Transport):
    @property
    def kind(self) -> str:
        return "Велосипед"


class Item(Asset, ABC):
    """Вещь на балансе, которую нельзя выдать как прокатный транспорт."""


@dataclass(frozen=True, kw_only=True)
class Helmet(Item):
    @property
    def kind(self) -> str:
        return "Шлем"


@dataclass(frozen=True, kw_only=True)
class DockStation(Item):
    @property
    def kind(self) -> str:
        return "Док-станция (без зарядки)"


@dataclass(frozen=True, kw_only=True)
class ChargingCabinet(Item):
    daily_energy_kwh: float

    def __post_init__(self) -> None:
        super().__post_init__()
        validate_energy(self.daily_energy_kwh)

    @property
    def kind(self) -> str:
        return "Зарядный шкаф"
