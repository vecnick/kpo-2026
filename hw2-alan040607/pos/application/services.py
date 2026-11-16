from collections.abc import Callable, Iterable

from pos.domain.models import Criterion, Evaluation, Gradebook, NumericInput, Student
from pos.domain.ports import GradebookRepository
from pos.domain.rubric_builder import RubricBuilder
from pos.domain.scoring import FinalScore, FinalScoreService

from .ports import ReportExporter, ReportRow


class PosApplicationService:
    """Facade: coordinates persistence, domain operations and export."""

    def __init__(self, repository: GradebookRepository, scoring: FinalScoreService,
                 exporter: ReportExporter) -> None:
        self._repository = repository
        self._scoring = scoring
        self._exporter = exporter

    def _change(self, operation: Callable[[Gradebook], None]) -> None:
        book = self._repository.load()
        operation(book)
        self._repository.save(book)

    def add_student(self, student_id: str, name: str) -> None:
        self._change(lambda book: book.add_student(Student(student_id, name)))

    def add_criterion(self, criterion_id: str, name: str,
                      maximum: NumericInput, weight: NumericInput) -> None:
        self.add_rubric([(criterion_id, name, maximum, weight)])

    def add_rubric(self, entries: Iterable[tuple[str, str, NumericInput, NumericInput]]) -> None:
        builder = RubricBuilder()
        for entry in entries:
            builder.add(*entry)
        criteria = builder.build()
        self._change(lambda book: book.add_criteria(criteria))

    def assign_evaluation(self, student_id: str, criterion_id: str,
                          score: NumericInput, comment: str = '') -> None:
        self._change(lambda book: book.assign_evaluation(student_id, criterion_id, score, comment))

    def revise_evaluation(self, student_id: str, criterion_id: str,
                          score: NumericInput, comment: str = '') -> None:
        self._change(lambda book: book.revise_evaluation(student_id, criterion_id, score, comment))

    def revoke_evaluation(self, student_id: str, criterion_id: str) -> None:
        self._change(lambda book: book.revoke_evaluation(student_id, criterion_id))

    def students(self) -> tuple[Student, ...]:
        return tuple(self._repository.load().students.values())

    def criteria(self) -> tuple[Criterion, ...]:
        return tuple(self._repository.load().criteria.values())

    def evaluations(self, student_id: str) -> tuple[Evaluation, ...]:
        book = self._repository.load()
        book.require_student(student_id)
        return tuple(value for (sid, _), value in book.evaluations.items() if sid == student_id)

    def final_score(self, student_id: str) -> FinalScore:
        return self._scoring.calculate(self._repository.load(), student_id)

    def report(self) -> tuple[ReportRow, ...]:
        book = self._repository.load()
        return tuple(ReportRow(student.id, student.name, self._scoring.calculate(book, student.id))
                     for student in sorted(book.students.values(), key=lambda item: item.id))

    def export_report(self, destination: str) -> None:
        self._exporter.export(self.report(), destination)
