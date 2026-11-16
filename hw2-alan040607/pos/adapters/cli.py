import argparse
from collections.abc import Callable

from pos.application.ports import StorageError
from pos.application.services import PosApplicationService
from pos.domain.errors import DomainError
from pos.domain.models import NumericInput
from pos.domain.scoring import FinalScore

from .csv_exporter import display_score


def parser() -> argparse.ArgumentParser:
    root = argparse.ArgumentParser(description='ПОС: оценивание студентов одного курса')
    root.add_argument('--data', default='data/gradebook.json', help='Путь к JSON-хранилищу')
    root.add_argument('--policy', choices=('zero', 'strict'), default='zero',
                      help='Пропуски: zero — ноль, strict — итог только при всех оценках')
    commands = root.add_subparsers(dest='command')
    student = commands.add_parser('add-student', help='Добавить студента')
    student.add_argument('id')
    student.add_argument('name')
    criterion = commands.add_parser('add-criterion', help='Добавить критерий')
    for name in ('id', 'name', 'maximum', 'weight'):
        criterion.add_argument(name)
    for name in ('grade', 'revise'):
        command = commands.add_parser(name, help='Выставить / исправить оценку')
        command.add_argument('student')
        command.add_argument('criterion')
        command.add_argument('score')
        command.add_argument('--comment', default='')
    revoke = commands.add_parser('revoke', help='Отменить оценку')
    revoke.add_argument('student')
    revoke.add_argument('criterion')
    for name in ('scores', 'total'):
        command = commands.add_parser(name)
        command.add_argument('student')
    for name in ('students', 'criteria', 'report'):
        commands.add_parser(name)
    export = commands.add_parser('export', help='Экспорт CSV')
    export.add_argument('destination')
    return root


def show_result(result: FinalScore) -> str:
    return (f"{display_score(result.value) if result.value is not None else '—'} / 10"
            f" | {result.status} | пропусков: {result.missing_count}")


def execute(app: PosApplicationService, args: argparse.Namespace,
            write: Callable[[str], None] = print) -> None:
    command = args.command
    if command == 'add-student':
        app.add_student(args.id, args.name)
    elif command == 'add-criterion':
        app.add_criterion(args.id, args.name, args.maximum, args.weight)
    elif command in ('grade', 'revise'):
        method = app.assign_evaluation if command == 'grade' else app.revise_evaluation
        method(args.student, args.criterion, args.score, args.comment)
    elif command == 'revoke':
        app.revoke_evaluation(args.student, args.criterion)
    elif command == 'students':
        students = app.students()
        for student in students:
            write(f'{student.id} | {student.name}')
        if not students:
            write('Студентов пока нет.')
        return
    elif command == 'criteria':
        criteria = app.criteria()
        for criterion in criteria:
            write(f'{criterion.id} | {criterion.name} | максимум: {criterion.max_score}'
                  f' | вес: {criterion.weight.value}')
        if not criteria:
            write('Критериев пока нет.')
        return
    elif command == 'scores':
        evaluations = app.evaluations(args.student)
        for evaluation in evaluations:
            write(f'{evaluation.criterion_id} | {evaluation.score.value} | {evaluation.comment}')
        if not evaluations:
            write('Оценок пока нет.')
        return
    elif command == 'total':
        write(show_result(app.final_score(args.student)))
        return
    elif command == 'report':
        rows = app.report()
        for row in rows:
            write(f'{row.student_id} | {row.name} | {show_result(row.result)}')
        if not rows:
            write('Ведомость пуста.')
        return
    elif command == 'export':
        app.export_report(args.destination)
    write('Готово.')


def interactive(app: PosApplicationService, read: Callable[[str], str] = input,
                write: Callable[[str], None] = print) -> None:
    menu = (
        '\nПОС\n1. Добавить студента\n2. Студенты\n3. Добавить критерий'
        '\n4. Рубрикатор\n5. Выставить оценку\n6. Исправить оценку'
        '\n7. Отменить оценку\n8. Оценки студента\n9. Итог студента'
        '\n10. Общая ведомость\n11. Экспорт CSV\n12. Собрать рубрикатор\n0. Выход'
    )
    commands: dict[str, tuple[str, list[tuple[str, str]]]] = {
        '1': ('add-student', [('id', 'ID студента'), ('name', 'Имя')]),
        '2': ('students', []),
        '3': ('add-criterion', [('id', 'ID критерия'), ('name', 'Название'),
                                ('maximum', 'Максимум'), ('weight', 'Вес')]),
        '4': ('criteria', []),
        '5': ('grade', [('student', 'ID студента'), ('criterion', 'ID критерия'),
                        ('score', 'Балл'), ('comment', 'Комментарий (необязательно)')]),
        '6': ('revise', [('student', 'ID студента'), ('criterion', 'ID критерия'),
                         ('score', 'Балл'), ('comment', 'Комментарий (необязательно)')]),
        '7': ('revoke', [('student', 'ID студента'), ('criterion', 'ID критерия')]),
        '8': ('scores', [('student', 'ID студента')]),
        '9': ('total', [('student', 'ID студента')]),
        '10': ('report', []),
        '11': ('export', [('destination', 'Путь CSV')]),
    }
    try:
        while True:
            write(menu)
            choice = read('> ').strip()
            if choice == '0':
                return
            try:
                if choice == '12':
                    entries: list[tuple[str, str, NumericInput, NumericInput]] = []
                    write('Введите критерии. Пустой ID завершает сборку. Сохранение целиком.')
                    while True:
                        criterion_id = read('ID критерия: ').strip()
                        if not criterion_id:
                            break
                        entries.append((criterion_id, read('Название: '),
                                        read('Максимум: '), read('Вес: ')))
                    app.add_rubric(entries)
                    write('Рубрикатор сохранён.')
                elif choice in commands:
                    command, fields = commands[choice]
                    values = {key: read(label + ': ').strip() for key, label in fields}
                    execute(app, argparse.Namespace(command=command, **values), write)
                else:
                    write('Неизвестный пункт меню.')
            except (DomainError, StorageError) as error:
                write(f'Ошибка: {error}')
    except (EOFError, KeyboardInterrupt):
        write('\nРабота завершена.')
