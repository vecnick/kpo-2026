import ast
import csv
import json
import os
import random
import subprocess
import sys
import tempfile
import unittest
from dataclasses import FrozenInstanceError, asdict
from decimal import Decimal
from fractions import Fraction
from pathlib import Path
from unittest.mock import patch

from pos.adapters.cli import interactive
from pos.adapters.csv_exporter import CsvReportExporter, display_score
from pos.adapters.json_repository import InMemoryGradebookRepository, JsonGradebookRepository
from pos.application.ports import StorageError
from pos.application.services import PosApplicationService
from pos.bootstrap import Container, build_container
from pos.domain.errors import DomainError
from pos.domain.models import Criterion, Gradebook, Score, Student, Weight
from pos.domain.rubric_builder import RubricBuilder
from pos.domain.scoring import FinalScoreService, MissingAsZeroPolicy, RequireCompletePolicy


def sample_book():
    book = Gradebook()
    book.add_student(Student('s1', 'Анна'))
    book.add_criteria(RubricBuilder().add('hw', 'ДЗ', '20', '0.4')
                      .add('exam', 'Экзамен', '10', '0.6').build())
    return book


class DomainTests(unittest.TestCase):
    def setUp(self):
        self.book = sample_book()
        self.scoring = FinalScoreService(MissingAsZeroPolicy())

    def test_score_boundaries(self):
        for score in ('0', '20'):
            with self.subTest(score=score):
                book = sample_book()
                book.assign_evaluation('s1', 'hw', score)
                self.assertEqual(book.evaluations['s1', 'hw'].score.value, Decimal(score))

    def test_invalid_scores(self):
        for score in ('-1', '20.1', 'NaN', 'Infinity', '-Infinity', 'text',
                      '0.0000001', '1e999999999', True):
            with self.subTest(score=score), self.assertRaises(DomainError):
                self.book.assign_evaluation('s1', 'hw', score)
        self.assertFalse(self.book.evaluations)

    def test_positive_weight_and_maximum(self):
        for value in ('0', '-1', 'NaN', 'Infinity'):
            with self.subTest(value=value):
                with self.assertRaises(DomainError):
                    Weight(value)
                with self.assertRaises(DomainError):
                    Criterion('id', 'Name', value, Weight('1'))

    def test_student_and_criterion_must_exist(self):
        for student, criterion in (('other', 'hw'), ('s1', 'other')):
            with self.subTest(student=student, criterion=criterion), self.assertRaises(DomainError):
                self.book.assign_evaluation(student, criterion, '1')

    def test_duplicate_entities(self):
        with self.assertRaises(DomainError):
            self.book.add_student(Student('s1', 'Другое имя'))
        with self.assertRaises(DomainError):
            self.book.add_criterion(Criterion('hw', 'Другое имя', '10', Weight('1')))

    def test_student_identity_and_hash_depend_only_on_id(self):
        original = Student('s1', 'Анна')
        renamed = Student('s1', 'Анна Иванова')
        self.assertEqual(original, renamed)
        self.assertEqual(hash(original), hash(renamed))
        self.assertEqual(len({original, renamed}), 1)
        self.assertEqual({original: 'record'}[renamed], 'record')
        self.assertNotEqual(original, Student('s2', 'Анна'))

    def test_criterion_identity_ignores_state(self):
        original = Criterion('hw', 'ДЗ', '20', Weight('0.4'))
        changed = Criterion('hw', 'Новое название', '30', Weight('0.6'))
        self.assertEqual(original, changed)
        self.assertEqual(hash(original), hash(changed))
        self.assertEqual(len({original, changed}), 1)
        self.assertEqual({original: 'record'}[changed], 'record')
        self.assertNotEqual(original, Criterion('exam', 'ДЗ', '20', Weight('0.4')))

    def test_entity_types_do_not_share_identity(self):
        student = Student('same', 'Same')
        criterion = Criterion('same', 'Same', '10', Weight('1'))
        self.assertNotEqual(student, criterion)
        self.assertNotEqual(criterion, student)
        self.assertNotEqual(student, 'same')
        self.assertEqual(len({student, criterion}), 2)

    def test_value_objects_keep_value_equality(self):
        self.assertEqual(Score('1.0'), Score(1))
        self.assertNotEqual(Score(1), Score(2))
        self.assertEqual(Weight('0.4'), Weight(Decimal('0.4')))
        self.assertNotEqual(Weight('0.4'), Weight('0.6'))

    def test_numeric_constructors_store_decimal(self):
        for value in ('2', 2, Decimal('2')):
            with self.subTest(value=value):
                self.assertIsInstance(Score(value).value, Decimal)
                self.assertIsInstance(Weight(value).value, Decimal)
                self.assertIsInstance(Criterion('c', 'C', value, Weight(1)).max_score, Decimal)

    def test_nonempty_and_control_characters(self):
        for name in ('', '   ', 'a\nb'):
            with self.subTest(name=name), self.assertRaises(DomainError):
                Student('s', name)

    def test_duplicate_evaluation_requires_explicit_revision(self):
        self.book.assign_evaluation('s1', 'hw', '16')
        with self.assertRaises(DomainError):
            self.book.assign_evaluation('s1', 'hw', '17')
        self.assertEqual(self.book.evaluations['s1', 'hw'].score, Score('16'))

    def test_revision_and_revocation_update_total(self):
        self.book.assign_evaluation('s1', 'hw', '16')
        self.book.assign_evaluation('s1', 'exam', '9')
        self.assertEqual(self.scoring.calculate(self.book, 's1').value, Decimal('8.6'))
        self.book.revise_evaluation('s1', 'exam', '10', 'Исправлено')
        self.assertEqual(self.scoring.calculate(self.book, 's1').value, Decimal('9.2'))
        self.book.revoke_evaluation('s1', 'exam')
        self.assertEqual(self.scoring.calculate(self.book, 's1').value, Decimal('3.2'))

    def test_missing_revision_and_revocation(self):
        with self.assertRaises(DomainError):
            self.book.revise_evaluation('s1', 'hw', '1')
        with self.assertRaises(DomainError):
            self.book.revoke_evaluation('s1', 'hw')

    def test_invalid_revision_preserves_old_score(self):
        self.book.assign_evaluation('s1', 'hw', '16')
        with self.assertRaises(DomainError):
            self.book.revise_evaluation('s1', 'hw', '21')
        self.assertEqual(self.book.evaluations['s1', 'hw'].score, Score('16'))

    def test_model_is_not_mutable_through_public_views(self):
        with self.assertRaises(TypeError):
            self.book.students['x'] = Student('x', 'X')
        with self.assertRaises(FrozenInstanceError):
            self.book.students['s1'].name = ''

    def test_builder_snapshots_and_duplicates(self):
        builder = RubricBuilder().add('a', 'A', '10', '1')
        snapshot = builder.build()
        builder.add('b', 'B', '10', '2')
        self.assertEqual(len(snapshot), 1)
        with self.assertRaises(DomainError):
            builder.add('a', 'A', '10', '1')
        with self.assertRaises(DomainError):
            RubricBuilder().build()

    def test_rubric_batch_is_atomic(self):
        criteria = (RubricBuilder().add('new', 'New', '1', '1')
                    .add('hw', 'Duplicate', '1', '1').build())
        with self.assertRaises(DomainError):
            self.book.add_criteria(criteria)
        self.assertNotIn('new', self.book.criteria)

    def test_policies_for_missing_scores(self):
        self.book.assign_evaluation('s1', 'hw', '16')
        self.assertEqual(self.scoring.calculate(self.book, 's1').value, Decimal('3.2'))
        strict = FinalScoreService(RequireCompletePolicy())
        self.assertIsNone(strict.calculate(self.book, 's1').value)
        self.book.assign_evaluation('s1', 'exam', '9')
        self.assertEqual(strict.calculate(self.book, 's1').value, Decimal('8.6'))

    def test_no_criteria_is_not_a_zero(self):
        book = Gradebook()
        book.add_student(Student('s', 'S'))
        result = self.scoring.calculate(book, 's')
        self.assertIsNone(result.value)
        self.assertEqual(result.status, 'Нет критериев')

    def test_weight_normalization_and_full_marks(self):
        book = Gradebook()
        book.add_student(Student('s', 'S'))
        for key, maximum, weight in (('a', '3', '2'), ('b', '7', '3')):
            book.add_criterion(Criterion(key, key, maximum, Weight(weight)))
            book.assign_evaluation('s', key, maximum)
        self.assertEqual(self.scoring.calculate(book, 's').value, Decimal('10'))

    def test_rounding_is_only_for_presentation(self):
        self.assertEqual(display_score(Decimal('8.605')), '8.61')
        self.assertEqual(display_score(None), '')

    def test_scoring_against_independent_fraction_formula(self):
        rng = random.Random(42)
        for case in range(100):
            with self.subTest(case=case):
                book = Gradebook()
                book.add_student(Student('s', 'Student'))
                weighted = Fraction(0)
                total_weight = 0
                for index in range(rng.randint(1, 8)):
                    maximum, weight = rng.randint(1, 100), rng.randint(1, 100)
                    score = rng.randint(0, maximum)
                    key = str(index)
                    book.add_criterion(Criterion(key, key, maximum, Weight(weight)))
                    total_weight += weight
                    if rng.choice((True, False)):
                        book.assign_evaluation('s', key, score)
                        weighted += Fraction(weight * score, maximum)
                expected = 10 * weighted / total_weight
                actual = self.scoring.calculate(book, 's').value
                self.assertLessEqual(abs(Fraction(actual) - expected), Fraction(1, 10**45))
                self.assertTrue(0 <= actual <= 10)


class AdapterTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.path = Path(self.temp.name) / 'book.json'
        self.repo = JsonGradebookRepository(self.path)
        self.app = PosApplicationService(self.repo, FinalScoreService(MissingAsZeroPolicy()),
                                         CsvReportExporter(self.path))

    def test_missing_file_is_empty(self):
        self.assertFalse(self.repo.load().students)

    def test_roundtrip(self):
        book = sample_book()
        book.assign_evaluation('s1', 'hw', '16.25', 'Хорошо')
        self.repo.save(book)
        restored = self.repo.load()
        self.assertEqual(restored.students, book.students)
        self.assertEqual(restored.criteria, book.criteria)
        self.assertEqual(restored.evaluations, book.evaluations)
        self.assertEqual({key: asdict(value) for key, value in restored.students.items()},
                         {key: asdict(value) for key, value in book.students.items()})
        self.assertEqual({key: asdict(value) for key, value in restored.criteria.items()},
                         {key: asdict(value) for key, value in book.criteria.items()})

    def test_corruption_is_not_overwritten(self):
        self.path.write_text('{broken', encoding='utf-8')
        with self.assertRaises(StorageError):
            self.app.add_student('s', 'S')
        self.assertEqual(self.path.read_text(encoding='utf-8'), '{broken')

    def test_invalid_persisted_domain_is_rejected(self):
        self.repo.save(sample_book())
        document = json.loads(self.path.read_text(encoding='utf-8'))
        document['evaluations'] = [dict(student_id='s1', criterion_id='hw', score='21', comment='')]
        self.path.write_text(json.dumps(document), encoding='utf-8')
        with self.assertRaises(StorageError):
            self.repo.load()

    def test_unknown_schema_and_duplicate_ids_rejected(self):
        for document in ({'version': 2, 'students': [], 'criteria': [], 'evaluations': []},
                         {'version': 1, 'students': [{'id': 's', 'name': 'S'}] * 2,
                          'criteria': [], 'evaluations': []}):
            with self.subTest(document=document):
                self.path.write_text(json.dumps(document), encoding='utf-8')
                with self.assertRaises(StorageError):
                    self.repo.load()

    def test_failed_replace_preserves_file(self):
        self.repo.save(sample_book())
        before = self.path.read_bytes()
        with patch('pos.adapters.json_repository.os.replace', side_effect=OSError('locked')):
            with self.assertRaises(StorageError):
                self.repo.save(Gradebook())
        self.assertEqual(self.path.read_bytes(), before)
        self.assertEqual(list(self.path.parent.iterdir()), [self.path])

    def test_memory_repository_returns_detached_objects(self):
        repo = InMemoryGradebookRepository()
        repo.save(sample_book())
        repo.load().add_student(Student('other', 'Other'))
        self.assertNotIn('other', repo.load().students)

    def test_application_batch_failure_does_not_save(self):
        self.app.add_criterion('hw', 'HW', '20', '1')
        with self.assertRaises(DomainError):
            self.app.add_rubric([('new', 'New', '10', '1'), ('hw', 'HW', '10', '1')])
        self.assertEqual([c.id for c in self.app.criteria()], ['hw'])

    def test_csv_unicode_quoting_and_incomplete_result(self):
        name = 'Анна; "А"'
        self.app.add_student('s', name)
        destination = self.path.with_suffix('.csv')
        self.app.export_report(str(destination))
        with destination.open(encoding='utf-8-sig', newline='') as stream:
            rows = list(csv.DictReader(stream, delimiter=';'))
        self.assertEqual(rows[0]['name'], name)
        self.assertEqual(rows[0]['final_score'], '')
        self.assertEqual(rows[0]['status'], 'Нет критериев')

    def test_export_cannot_overwrite_database(self):
        self.repo.save(sample_book())
        before = self.path.read_bytes()
        with self.assertRaises(StorageError):
            self.app.export_report(str(self.path))
        self.assertEqual(self.path.read_bytes(), before)

    def test_csv_formula_is_escaped(self):
        self.app.add_student('s', '=1+1')
        destination = self.path.with_suffix('.csv')
        self.app.export_report(str(destination))
        with destination.open(encoding='utf-8-sig', newline='') as stream:
            rows = list(csv.DictReader(stream, delimiter=';'))
        self.assertEqual(rows[0]['name'], "'=1+1")

    def test_failed_csv_write_preserves_previous_report(self):
        self.app.add_student('s', 'Student')
        destination = self.path.with_suffix('.csv')
        destination.write_bytes(b'previous report')
        with patch('pos.adapters.csv_exporter.csv.writer') as writer:
            writer.return_value.writerow.side_effect = [None, OSError('disk full')]
            with self.assertRaises(StorageError):
                self.app.export_report(str(destination))
        self.assertEqual(destination.read_bytes(), b'previous report')
        self.assertEqual(set(self.path.parent.iterdir()), {self.path, destination})

    def test_invalid_export_path_is_a_storage_error(self):
        with self.assertRaises(StorageError):
            self.app.export_report('invalid\0.csv')

    def test_failed_csv_replace_preserves_previous_report(self):
        destination = self.path.with_suffix('.csv')
        destination.write_bytes(b'previous report')
        with patch('pos.adapters.csv_exporter.os.replace', side_effect=OSError('locked')):
            with self.assertRaises(StorageError):
                self.app.export_report(str(destination))
        self.assertEqual(destination.read_bytes(), b'previous report')
        self.assertEqual(list(self.path.parent.iterdir()), [destination])

    def test_interactive_builder_persists_complete_rubric(self):
        answers = iter(['12', 'hw', 'Homework', '20', '0.4',
                        'exam', 'Exam', '10', '0.6', '', '0'])
        interactive(self.app, lambda _: next(answers), lambda _: None)
        self.assertEqual([criterion.id for criterion in self.app.criteria()], ['hw', 'exam'])

    def test_interactive_builder_rejects_entire_invalid_rubric(self):
        answers = iter(['12', 'hw', 'Homework', '20', '0.4',
                        'exam', 'Exam', '0', '0.6', '', '0'])
        output = []
        interactive(self.app, lambda _: next(answers), output.append)
        self.assertTrue(any('Ошибка:' in line for line in output))
        self.assertFalse(self.app.criteria())

    def test_interactive_eof_exits_cleanly(self):
        output = []
        with patch('builtins.input', side_effect=EOFError) as read:
            interactive(self.app, read, output.append)
        self.assertIn('Работа завершена.', output[-1])

    def test_interactive_recovers_after_invalid_input(self):
        answers = iter(['bad', '1', 's', 'Student', '3', 'c', 'Criterion', 'no', '1', '2', '0'])
        output = []
        interactive(self.app, lambda _: next(answers), output.append)
        self.assertTrue(any('Неизвестный' in line for line in output))
        self.assertTrue(any('Ошибка:' in line for line in output))
        self.assertTrue(any('s | Student' in line for line in output))


class SystemTests(unittest.TestCase):
    def test_demo_in_clean_directory(self):
        with tempfile.TemporaryDirectory() as directory:
            environment = dict(os.environ, PYTHONPATH=str(Path.cwd()))
            result = subprocess.run([sys.executable, '-X', 'utf8', '-m', 'examples.demo'],
                                    cwd=directory, env=environment, text=True,
                                    encoding='utf-8', capture_output=True)
            self.assertEqual(result.returncode, 0, result.stdout + result.stderr)
            self.assertIn('Демонстрация пройдена', result.stdout)
            self.assertEqual(len(list(Path(directory).glob('output/demo-*/report.csv'))), 1)

    def test_application_does_not_import_adapters(self):
        for path in Path('pos/application').glob('*.py'):
            for node in ast.walk(ast.parse(path.read_text(encoding='utf-8'))):
                if isinstance(node, ast.Import):
                    modules = [alias.name for alias in node.names]
                elif isinstance(node, ast.ImportFrom):
                    modules = [node.module or '']
                else:
                    continue
                for module in modules:
                    self.assertFalse(module.startswith(('pos.adapters', 'pos.bootstrap')))

    def test_container_singletons_and_errors(self):
        container = Container()
        container.register('x', lambda _: object())
        self.assertIs(container.resolve('x'), container.resolve('x'))
        with self.assertRaises(ValueError):
            container.register('x', lambda _: object())
        with self.assertRaises(ValueError):
            container.resolve('missing')
        container.register('cycle', lambda c: c.resolve('cycle'))
        with self.assertRaises(ValueError):
            container.resolve('cycle')

    def test_composition_root_resolves_application(self):
        app = build_container(policy='strict').resolve(PosApplicationService)
        self.assertIsInstance(app, PosApplicationService)

    def test_domain_has_no_outer_imports(self):
        for path in Path('pos/domain').glob('*.py'):
            tree = ast.parse(path.read_text(encoding='utf-8'))
            for node in ast.walk(tree):
                modules = []
                if isinstance(node, ast.Import):
                    modules = [alias.name for alias in node.names]
                elif isinstance(node, ast.ImportFrom):
                    modules = [node.module or '']
                for module in modules:
                    forbidden = ('pos.adapters', 'pos.application', 'pos.bootstrap',
                                 'csv', 'json', 'argparse')
                    self.assertFalse(module.startswith(forbidden))

    def test_cli_end_to_end_across_process_restarts(self):
        with tempfile.TemporaryDirectory() as directory:
            data = str(Path(directory) / 'book.json')
            destination = str(Path(directory) / 'report.csv')

            def run(*args, expected=0):
                result = subprocess.run([sys.executable, '-X', 'utf8', '-m', 'pos', '--data', data,
                                         *args], text=True, encoding='utf-8', capture_output=True)
                self.assertEqual(result.returncode, expected, result.stdout + result.stderr)
                return result.stdout + result.stderr

            run('add-student', 's', 'Анна')
            run('add-criterion', 'hw', 'ДЗ', '20', '0.4')
            run('add-criterion', 'exam', 'Экзамен', '10', '0.6')
            self.assertIn('Ошибка:', run('grade', 's', 'hw', '21', expected=1))
            run('grade', 's', 'hw', '16')
            run('grade', 's', 'exam', '9')
            self.assertIn('8.60', run('total', 's'))
            run('revise', 's', 'exam', '10')
            self.assertIn('9.20', run('report'))
            run('revoke', 's', 'exam')
            self.assertIn('3.20', run('total', 's'))
            self.assertIn('Не все оценки', run('--policy', 'strict', 'total', 's'))
            run('export', destination)
            self.assertTrue(Path(destination).exists())


if __name__ == '__main__':
    unittest.main()
