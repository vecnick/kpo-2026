from collections.abc import Callable, Hashable
from os import PathLike
from typing import Any

from pos.adapters.csv_exporter import CsvReportExporter
from pos.adapters.json_repository import JsonGradebookRepository
from pos.application.ports import ReportExporter
from pos.application.services import PosApplicationService
from pos.domain.ports import GradebookRepository
from pos.domain.scoring import (FinalScoreService, MissingAsZeroPolicy, MissingPolicy,
                                RequireCompletePolicy)


class Container:
    """Minimal DI container: lazy singleton providers and constructor injection."""

    def __init__(self) -> None:
        self._providers: dict[Hashable, Callable[['Container'], object]] = {}
        self._instances: dict[Hashable, object] = {}
        self._resolving: set[Hashable] = set()

    def register(self, key: Hashable, provider: Callable[['Container'], object]) -> None:
        if key in self._providers:
            raise ValueError(f'Duplicate registration: {key}')
        self._providers[key] = provider

    def resolve(self, key: Hashable) -> Any:
        # Registry keys may be protocols or strings; concrete services are typed at injection.
        if key in self._instances:
            return self._instances[key]
        if key in self._resolving:
            raise ValueError(f'Circular dependency: {key}')
        if key not in self._providers:
            raise ValueError(f'Unregistered dependency: {key}')
        self._resolving.add(key)
        try:
            instance = self._providers[key](self)
            self._instances[key] = instance
            return instance
        finally:
            self._resolving.remove(key)


def build_container(path: str | PathLike[str] = 'data/gradebook.json',
                    policy: str = 'zero') -> Container:
    policies: dict[str, Callable[[], MissingPolicy]] = {
        'zero': MissingAsZeroPolicy, 'strict': RequireCompletePolicy,
    }
    if policy not in policies:
        raise ValueError('Unknown scoring policy')
    container = Container()
    container.register(GradebookRepository, lambda _: JsonGradebookRepository(path))
    container.register(ReportExporter, lambda _: CsvReportExporter(path))
    container.register(MissingPolicy, lambda _: policies[policy]())
    container.register(FinalScoreService, lambda c: FinalScoreService(c.resolve(MissingPolicy)))
    container.register(PosApplicationService, lambda c: PosApplicationService(
        c.resolve(GradebookRepository), c.resolve(FinalScoreService), c.resolve(ReportExporter)))
    return container

