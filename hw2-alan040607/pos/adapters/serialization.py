from collections.abc import Iterable
from typing import Any, cast

from pos.domain.models import Criterion, Gradebook, Student, Weight


def to_document(book: Gradebook) -> dict[str, Any]:
    return {
        'version': 1,
        'students': [{'id': s.id, 'name': s.name} for s in book.students.values()],
        'criteria': [{'id': c.id, 'name': c.name, 'max_score': str(c.max_score),
                      'weight': str(c.weight.value)} for c in book.criteria.values()],
        'evaluations': [{'student_id': e.student_id, 'criterion_id': e.criterion_id,
                         'score': str(e.score.value), 'comment': e.comment}
                        for e in book.evaluations.values()],
    }


def exact_keys(value: object, keys: Iterable[str]) -> dict[str, Any]:
    """Validate the shape of untrusted JSON before reading its dynamic values."""
    if not isinstance(value, dict) or set(value) != set(keys):
        raise ValueError('Некорректные поля документа.')
    return cast(dict[str, Any], value)


def from_document(document: object) -> Gradebook:
    document = exact_keys(document, ('version', 'students', 'criteria', 'evaluations'))
    if type(document['version']) is not int or document['version'] != 1:
        raise ValueError('Неподдерживаемая версия хранилища.')
    if any(not isinstance(document[key], list) for key in ('students', 'criteria', 'evaluations')):
        raise ValueError('Ожидаются списки студентов, критериев и оценок.')
    book = Gradebook()
    for student in document['students']:
        student = exact_keys(student, ('id', 'name'))
        book.add_student(Student(**student))
    for criterion in document['criteria']:
        criterion = exact_keys(criterion, ('id', 'name', 'max_score', 'weight'))
        book.add_criterion(Criterion(criterion['id'], criterion['name'],
                                    criterion['max_score'], Weight(criterion['weight'])))
    for evaluation in document['evaluations']:
        evaluation = exact_keys(evaluation, ('student_id', 'criterion_id', 'score', 'comment'))
        book.assign_evaluation(**evaluation)
    return book
