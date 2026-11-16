from typing import Self

from .errors import DomainError
from .models import Criterion, NumericInput, Weight


class RubricBuilder:
    """Build a nonempty rubric incrementally, without partial aggregate updates."""

    def __init__(self) -> None:
        self._criteria: dict[str, Criterion] = {}

    def add(self, criterion_id: str, name: str,
            max_score: NumericInput, weight: NumericInput) -> Self:
        criterion = Criterion(criterion_id, name, max_score, Weight(weight))
        if criterion.id in self._criteria:
            raise DomainError("Повторный ID в создаваемом рубрикаторе.")
        self._criteria[criterion.id] = criterion
        return self

    def build(self) -> tuple[Criterion, ...]:
        if not self._criteria:
            raise DomainError("Рубрикатор не может быть пустым.")
        return tuple(self._criteria.values())
