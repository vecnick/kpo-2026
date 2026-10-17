"""Единственное место сборки графа зависимостей приложения."""

from dependency_injector import containers, providers

from vyshkat.cli import ConsoleApp
from vyshkat.fleet import FleetService
from vyshkat.inspection import ServiceCenter


class Container(containers.DeclarativeContainer):
    inspection = providers.Factory(ServiceCenter)
    fleet = providers.Singleton(FleetService, inspection=inspection)
    console = providers.Factory(ConsoleApp, fleet=fleet)
