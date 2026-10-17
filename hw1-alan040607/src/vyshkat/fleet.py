"""Приёмка и отчёты. Здесь нет консольного ввода и создания зависимостей."""

from dataclasses import dataclass
from math import fsum

from vyshkat.domain import Asset, EnergyConsumer, Inventoriable, Item, Transport
from vyshkat.inspection import InspectionResult, InspectionService


@dataclass(frozen=True)
class FleetReport:
    vehicle_count: int
    item_count: int
    vehicle_energy_kwh: float
    total_energy_kwh: float


class FleetService:
    def __init__(self, inspection: InspectionService) -> None:
        self._inspection = inspection
        self._assets: dict[str, Asset] = {}

    def register(self, asset: Asset) -> InspectionResult:
        """Отказ или ошибка не меняют баланс; номера уникальны среди всех объектов."""
        if not isinstance(asset, (Transport, Item)):
            raise ValueError("На баланс можно поставить только транспорт или вещь.")
        if asset.inventory_number in self._assets:
            raise ValueError(f"Инвентарный номер {asset.inventory_number} уже занят.")
        if isinstance(asset, Transport):
            result = self._inspection.inspect(asset)
            if not result.accepted:
                return result
        else:
            result = InspectionResult(True, "Вещь поставлена на баланс.")
        if isinstance(asset, EnergyConsumer):
            # Даже конечные входные числа могут переполнить суммарный float.
            try:
                fsum(
                    [asset.daily_energy_kwh]
                    + [
                        entry.daily_energy_kwh
                        for entry in self._assets.values()
                        if isinstance(entry, EnergyConsumer)
                    ]
                )
            except OverflowError:
                raise ValueError("Суммарный расход энергии слишком велик.") from None
        self._assets[asset.inventory_number] = asset
        return result

    def inventory(self) -> tuple[Inventoriable, ...]:
        return tuple(self._assets.values())

    def beginner_vehicles(self) -> tuple[Transport, ...]:
        return tuple(
            asset
            for asset in self._assets.values()
            if isinstance(asset, Transport) and asset.beginner_ease >= 6
        )

    def report(self) -> FleetReport:
        vehicles = tuple(a for a in self._assets.values() if isinstance(a, Transport))
        return FleetReport(
            vehicle_count=len(vehicles),
            item_count=len(self._assets) - len(vehicles),
            vehicle_energy_kwh=fsum(
                a.daily_energy_kwh for a in vehicles if isinstance(a, EnergyConsumer)
            ),
            total_energy_kwh=fsum(
                a.daily_energy_kwh for a in self._assets.values() if isinstance(a, EnergyConsumer)
            ),
        )
