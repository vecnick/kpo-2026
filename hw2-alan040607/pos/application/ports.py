from dataclasses import dataclass
from typing import Protocol

from pos.domain.scoring import FinalScore


class StorageError(Exception):
    """An adapter failed to read or write data."""


@dataclass(frozen=True)
class ReportRow:
    student_id: str
    name: str
    result: FinalScore


class ReportExporter(Protocol):
    def export(self, rows: tuple[ReportRow, ...], destination: str) -> None: ...
