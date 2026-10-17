from dataclasses import dataclass, replace
from unittest.mock import Mock

import pytest
from dependency_injector import providers

from vyshkat.domain import Bicycle, ChargingCabinet, Helmet, Transport
from vyshkat.inspection import InspectionResult, InspectionService, ServiceCenter


@pytest.mark.parametrize(
    "brakes,battery,accepted,reason",
    [
        (True, True, True, "пройден"),
        (False, True, False, "тормоза"),
        (True, False, False, "аккумулятор"),
        (False, False, False, "тормоза"),
    ],
)
def test_service_center_decision(scooter, brakes, battery, accepted, reason):
    result = ServiceCenter().inspect(replace(scooter, brakes_ok=brakes, battery_ok=battery))
    assert result.accepted is accepted
    assert reason in result.reason


def test_bicycle_inspection_does_not_require_battery():
    bicycle = Bicycle(inventory_number="B", name="Велосипед", beginner_ease=5)
    assert ServiceCenter().inspect(bicycle).accepted


@pytest.mark.parametrize("accepted", [True, False])
def test_di_override_controls_admission_and_all_reports(container, scooter, accepted):
    inspection = Mock(spec=InspectionService)
    inspection.inspect.return_value = InspectionResult(accepted, "Решение тестового сервиса")
    with container.inspection.override(providers.Object(inspection)):
        fleet = container.fleet()
        assert fleet.register(scooter) == inspection.inspect.return_value
        inspection.inspect.assert_called_once_with(scooter)
        assert fleet.inventory() == ((scooter,) if accepted else ())
        assert fleet.beginner_vehicles() == ((scooter,) if accepted else ())
        assert fleet.report().vehicle_count == int(accepted)
        assert fleet.report().total_energy_kwh == (2.5 if accepted else 0)


def test_inspection_exception_does_not_leave_partial_registration(container, scooter):
    inspection = Mock(spec=InspectionService)
    inspection.inspect.side_effect = RuntimeError("Сервис недоступен")
    with container.inspection.override(inspection):
        fleet = container.fleet()
        with pytest.raises(RuntimeError, match="недоступен"):
            fleet.register(scooter)
        assert fleet.inventory() == ()


def test_items_do_not_go_through_vehicle_inspection(container):
    inspection = Mock(spec=InspectionService)
    with container.inspection.override(inspection):
        fleet = container.fleet()
        assert fleet.register(Helmet(inventory_number="H", name="Шлем")).accepted
        inspection.inspect.assert_not_called()


@pytest.mark.parametrize("ease,expected", [(1, False), (5, False), (6, True), (10, True)])
def test_beginner_boundary(fleet, scooter, ease, expected):
    vehicle = replace(scooter, beginner_ease=ease)
    fleet.register(vehicle)
    fleet.register(Helmet(inventory_number="H", name="Шлем для новичка"))
    assert fleet.beginner_vehicles() == ((vehicle,) if expected else ())


def test_duplicate_number_across_categories_is_atomic(fleet, scooter):
    fleet.register(scooter)
    before = fleet.report()
    with pytest.raises(ValueError, match="уже занят"):
        fleet.register(Helmet(inventory_number=" S-1 ", name="Дубликат"))
    assert fleet.inventory() == (scooter,)
    assert fleet.report() == before


def test_rejected_number_can_be_used_after_repair(fleet, scooter):
    assert not fleet.register(replace(scooter, brakes_ok=False)).accepted
    assert fleet.register(scooter).accepted
    assert fleet.inventory() == (scooter,)


def test_empty_and_mixed_reports(fleet, scooter):
    assert fleet.report().vehicle_count == fleet.report().item_count == 0
    assert fleet.report().total_energy_kwh == fleet.report().vehicle_energy_kwh == 0
    fleet.register(scooter)
    fleet.register(Bicycle(inventory_number="B", name="Вело", beginner_ease=5))
    fleet.register(Helmet(inventory_number="H", name="Шлем"))
    fleet.register(ChargingCabinet(inventory_number="C", name="Шкаф", daily_energy_kwh=4))
    report = fleet.report()
    assert (report.vehicle_count, report.item_count) == (2, 2)
    assert report.vehicle_energy_kwh == 2.5
    assert report.total_energy_kwh == 6.5


def test_inventory_snapshot_cannot_mutate_balance(fleet, scooter):
    snapshot = fleet.inventory()
    fleet.register(scooter)
    assert snapshot == ()
    assert isinstance(fleet.inventory(), tuple)


def test_total_energy_overflow_is_rejected_without_corrupting_reports(fleet):
    first = ChargingCabinet(inventory_number="C-1", name="Шкаф", daily_energy_kwh=1e308)
    fleet.register(first)
    with pytest.raises(ValueError, match="слишком велик"):
        fleet.register(replace(first, inventory_number="C-2"))
    assert fleet.inventory() == (first,)
    assert fleet.report().total_energy_kwh == 1e308


def test_duplicate_transport_is_not_inspected_twice(container, scooter):
    inspection = Mock(spec=InspectionService)
    inspection.inspect.return_value = InspectionResult(True, "Принят")
    with container.inspection.override(inspection):
        fleet = container.fleet()
        fleet.register(scooter)
        with pytest.raises(ValueError, match="уже занят"):
            fleet.register(scooter)
        inspection.inspect.assert_called_once_with(scooter)


def test_new_transport_subclass_works_without_fleet_changes(fleet):
    @dataclass(frozen=True, kw_only=True)
    class Tricycle(Transport):
        daily_energy_kwh: float = 0.25

        @property
        def kind(self):
            return "Трицикл"

    vehicle = Tricycle(inventory_number="T", name="Трицикл", beginner_ease=8)
    assert fleet.register(vehicle).accepted
    assert fleet.beginner_vehicles() == (vehicle,)
    assert fleet.report().total_energy_kwh == 0.25


def test_employee_cannot_be_registered(fleet):
    @dataclass
    class Employee:
        name: str

    with pytest.raises(ValueError, match="только транспорт или вещь"):
        fleet.register(Employee("Иван"))
    assert fleet.inventory() == ()


def test_container_lifetime_and_isolation(container):
    from vyshkat.container import Container

    assert container.fleet() is container.fleet()
    assert container.console().fleet is container.fleet()
    assert Container().fleet() is not container.fleet()
