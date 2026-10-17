from dataclasses import FrozenInstanceError

import pytest

from vyshkat.domain import (
    Bicycle,
    ChargingCabinet,
    DockStation,
    ElectricBicycle,
    EnergyConsumer,
    Helmet,
    Inventoriable,
    Scooter,
)


@pytest.mark.parametrize("ease", [0, 11, -1, 1.5, True, "6", None])
def test_invalid_ease_rejected(ease):
    with pytest.raises(ValueError, match="от 1 до 10"):
        Bicycle(inventory_number="B", name="Велосипед", beginner_ease=ease)


@pytest.mark.parametrize("ease", [1, 6, 10])
def test_valid_ease_accepted(ease):
    assert Bicycle(inventory_number="B", name="Велосипед", beginner_ease=ease).beginner_ease == ease


@pytest.mark.parametrize("field", ["inventory_number", "name"])
@pytest.mark.parametrize("value", ["", "  ", None, 123])
def test_blank_or_nontext_identity_rejected(field, value):
    fields = {"inventory_number": "H", "name": "Шлем", field: value}
    with pytest.raises(ValueError, match="не могут быть пустыми"):
        Helmet(**fields)


def test_identity_trimmed_and_immutable():
    helmet = Helmet(inventory_number=" H ", name=" Шлем ")
    assert (helmet.inventory_number, helmet.name) == ("H", "Шлем")
    with pytest.raises(FrozenInstanceError):
        helmet.inventory_number = "NEW"


@pytest.mark.parametrize("energy", [-0.1, float("nan"), float("inf"), -float("inf"), True, "2"])
@pytest.mark.parametrize("constructor", [Scooter, ChargingCabinet])
def test_invalid_energy_rejected(energy, constructor):
    fields = {"inventory_number": "X", "name": "Объект", "daily_energy_kwh": energy}
    if constructor is Scooter:
        fields["beginner_ease"] = 6
    with pytest.raises(ValueError, match="Расход энергии"):
        constructor(**fields)


@pytest.mark.parametrize("energy", [0, 0.5, 12])
def test_valid_energy(energy):
    assert ChargingCabinet(inventory_number="C", name="Шкаф", daily_energy_kwh=energy)


def test_energy_integer_overflow_is_a_validation_error():
    with pytest.raises(ValueError, match="Расход энергии"):
        ChargingCabinet(inventory_number="C", name="Шкаф", daily_energy_kwh=10**1000)


@pytest.mark.parametrize("field", ["brakes_ok", "battery_ok"])
def test_technical_condition_must_be_boolean(field):
    with pytest.raises(ValueError, match="логическим"):
        Scooter(
            inventory_number="S",
            name="Самокат",
            beginner_ease=6,
            daily_energy_kwh=1,
            **{field: "да"},
        )


def test_capability_interfaces_do_not_force_energy_on_every_asset(scooter):
    assets = [
        scooter,
        ElectricBicycle(inventory_number="E", name="Электро", beginner_ease=6, daily_energy_kwh=1),
        Bicycle(inventory_number="B", name="Вело", beginner_ease=6),
        Helmet(inventory_number="H", name="Шлем"),
        DockStation(inventory_number="D", name="Док"),
        ChargingCabinet(inventory_number="C", name="Шкаф", daily_energy_kwh=4),
    ]
    assert all(isinstance(asset, Inventoriable) for asset in assets)
    assert [isinstance(asset, EnergyConsumer) for asset in assets] == [
        True,
        True,
        False,
        False,
        False,
        True,
    ]
    assert len({asset.kind for asset in assets}) == 6
