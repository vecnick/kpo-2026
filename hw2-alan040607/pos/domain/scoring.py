from dataclasses import dataclass
from decimal import Decimal, localcontext
from typing import Protocol

from .models import Gradebook


class MissingPolicy(Protocol):
    def permits(self, missing_count: int) -> bool: ...


class MissingAsZeroPolicy:
    def permits(self, missing_count: int) -> bool:
        return True


class RequireCompletePolicy:
    def permits(self, missing_count: int) -> bool:
        return missing_count == 0


@dataclass(frozen=True)
class FinalScore:
    value: Decimal | None
    status: str
    missing_count: int


class FinalScoreService:
    def __init__(self, policy: MissingPolicy) -> None:
        self._policy = policy

    def calculate(self, book: Gradebook, student_id: str) -> FinalScore:
        book.require_student(student_id)
        if not book.criteria:
            return FinalScore(None, 'Нет критериев', 0)
        missing = sum((student_id, key) not in book.evaluations for key in book.criteria)
        if not self._policy.permits(missing):
            return FinalScore(None, 'Не все оценки выставлены', missing)
        with localcontext() as context:
            context.prec = 50
            weighted = Decimal(0)
            total_weight = Decimal(0)
            for criterion in book.criteria.values():
                evaluation = book.evaluations.get((student_id, criterion.id))
                score = evaluation.score.value if evaluation else Decimal(0)
                weighted += criterion.weight.value * score / criterion.max_score
                total_weight += criterion.weight.value
            return FinalScore(Decimal(10) * weighted / total_weight, 'Рассчитан', missing)
