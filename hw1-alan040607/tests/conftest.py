import pytest

from vyshkat.container import Container
from vyshkat.domain import Scooter


@pytest.fixture
def scooter():
    return Scooter(inventory_number="S-1", name="Самокат", beginner_ease=6, daily_energy_kwh=2.5)


@pytest.fixture
def container():
    return Container()


@pytest.fixture
def fleet(container):
    return container.fleet()
