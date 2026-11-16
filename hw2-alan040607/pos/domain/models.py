from collections.abc import Mapping
from dataclasses import dataclass, field
from decimal import Decimal, InvalidOperation
from types import MappingProxyType

from .errors import DomainError

NumericInput = Decimal | str | int


def number(value: object) -> Decimal:
    try:
        result = Decimal(str(value))
    except (InvalidOperation, ValueError):
        raise DomainError("Ожидается число.") from None
    if not result.is_finite():
        raise DomainError("Число должно быть конечным.")
    # Bound input size and precision to keep Decimal arithmetic predictable.
    if result.copy_abs() > Decimal('1000000000') or result.as_tuple().exponent < -6:
        raise DomainError("Число: не более 1 000 000 000 и 6 знаков после запятой.")
    return result


def nonempty(value: str, label: str) -> str:
    if not isinstance(value, str) or not value.strip():
        raise DomainError(f"{label} не может быть пустым.")
    if any(ord(char) < 32 for char in value):
        raise DomainError(f"{label}: управляющие символы запрещены.")
    return value.strip()


@dataclass(frozen=True, init=False)
class Score:
    value: Decimal

    def __init__(self, value: NumericInput) -> None:
        value = number(value)
        if value < 0:
            raise DomainError("Балл не может быть отрицательным.")
        object.__setattr__(self, 'value', value)


@dataclass(frozen=True, init=False)
class Weight:
    value: Decimal

    def __init__(self, value: NumericInput) -> None:
        value = number(value)
        if value <= 0:
            raise DomainError("Вес должен быть положительным.")
        object.__setattr__(self, 'value', value)


@dataclass(frozen=True)
class Student:
    id: str
    name: str = field(compare=False)

    def __post_init__(self) -> None:
        object.__setattr__(self, 'id', nonempty(self.id, 'ID студента'))
        object.__setattr__(self, 'name', nonempty(self.name, 'Имя'))


@dataclass(frozen=True, init=False)
class Criterion:
    id: str
    name: str = field(compare=False)
    max_score: Decimal = field(compare=False)
    weight: Weight = field(compare=False)

    def __init__(self, id: str, name: str, max_score: NumericInput, weight: Weight) -> None:
        object.__setattr__(self, 'id', nonempty(id, 'ID критерия'))
        object.__setattr__(self, 'name', nonempty(name, 'Название'))
        maximum = number(max_score)
        if maximum <= 0:
            raise DomainError("Максимальный балл должен быть положительным.")
        if not isinstance(weight, Weight):
            raise DomainError("Вес должен быть объектом Weight.")
        object.__setattr__(self, 'max_score', maximum)
        object.__setattr__(self, 'weight', weight)


@dataclass(frozen=True)
class Evaluation:
    student_id: str
    criterion_id: str
    score: Score
    comment: str = ''

    def __post_init__(self) -> None:
        if not isinstance(self.score, Score) or not isinstance(self.comment, str):
            raise DomainError("Некорректная оценка или комментарий.")


class Gradebook:
    """Aggregate root for one course; all modifications protect invariants."""

    def __init__(self) -> None:
        self._students: dict[str, Student] = {}
        self._criteria: dict[str, Criterion] = {}
        self._evaluations: dict[tuple[str, str], Evaluation] = {}

    @property
    def students(self) -> Mapping[str, Student]:
        return MappingProxyType(self._students)

    @property
    def criteria(self) -> Mapping[str, Criterion]:
        return MappingProxyType(self._criteria)

    @property
    def evaluations(self) -> Mapping[tuple[str, str], Evaluation]:
        return MappingProxyType(self._evaluations)

    def require_student(self, student_id: str) -> Student:
        if student_id not in self._students:
            raise DomainError(f"Студент '{student_id}' не найден.")
        return self._students[student_id]

    def add_student(self, student: Student) -> None:
        if student.id in self._students:
            raise DomainError("ID студента уже существует.")
        self._students[student.id] = student

    def add_criterion(self, criterion: Criterion) -> None:
        self.add_criteria((criterion,))

    def add_criteria(self, criteria: tuple[Criterion, ...]) -> None:
        ids = [criterion.id for criterion in criteria]
        if len(set(ids)) != len(ids) or any(key in self._criteria for key in ids):
            raise DomainError("ID критерия уже существует.")
        self._criteria.update((criterion.id, criterion) for criterion in criteria)

    def _checked_evaluation(self, student_id: str, criterion_id: str,
                            score: NumericInput, comment: str) -> Evaluation:
        self.require_student(student_id)
        if criterion_id not in self._criteria:
            raise DomainError(f"Критерий '{criterion_id}' отсутствует в рубрикаторе.")
        checked_score = Score(score)
        if checked_score.value > self._criteria[criterion_id].max_score:
            raise DomainError("Балл превышает максимум критерия.")
        return Evaluation(student_id, criterion_id, checked_score, comment)

    def assign_evaluation(self, student_id: str, criterion_id: str,
                          score: NumericInput, comment: str = '') -> None:
        evaluation = self._checked_evaluation(student_id, criterion_id, score, comment)
        key = (student_id, criterion_id)
        if key in self._evaluations:
            raise DomainError("Оценка уже существует. Используйте исправление.")
        self._evaluations[key] = evaluation

    def revise_evaluation(self, student_id: str, criterion_id: str,
                          score: NumericInput, comment: str = '') -> None:
        evaluation = self._checked_evaluation(student_id, criterion_id, score, comment)
        key = (student_id, criterion_id)
        if key not in self._evaluations:
            raise DomainError("Оценка для исправления не найдена.")
        self._evaluations[key] = evaluation

    def revoke_evaluation(self, student_id: str, criterion_id: str) -> None:
        self.require_student(student_id)
        key = (student_id, criterion_id)
        if key not in self._evaluations:
            raise DomainError("Оценка для отмены не найдена.")
        del self._evaluations[key]
