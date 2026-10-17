"""Правила технической приёмки отделены от хранения парка и интерфейса."""

from dataclasses import dataclass
from typing import Protocol

from vyshkat.domain import ElectricTransport, Transport


@dataclass(frozen=True)
class InspectionResult:
    accepted: bool
    reason: str


class InspectionService(Protocol):
    def inspect(self, vehicle: Transport) -> InspectionResult: ...


class ServiceCenter:
    """Учебная детерминированная модель техосмотра по введённому состоянию."""

    def inspect(self, vehicle: Transport) -> InspectionResult:
        if not vehicle.brakes_ok:
            return InspectionResult(False, "Неисправны тормоза.")
        if isinstance(vehicle, ElectricTransport) and not vehicle.battery_ok:
            return InspectionResult(False, "Неисправен аккумулятор.")
        return InspectionResult(True, "Техосмотр пройден.")
