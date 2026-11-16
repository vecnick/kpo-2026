"""Run the public CLI end to end, preserving results in a new output directory."""
import csv
import subprocess
import sys
import tempfile
from pathlib import Path


def main() -> None:
    output = Path('output')
    output.mkdir(exist_ok=True)
    directory = Path(tempfile.mkdtemp(prefix='demo-', dir=output)).resolve()
    data = directory / 'gradebook.json'
    report = directory / 'report.csv'

    def run(*arguments: str, expected: int = 0) -> str:
        print('\n> python -m pos ' + ' '.join(arguments), flush=True)
        result = subprocess.run(
            [sys.executable, '-X', 'utf8', '-m', 'pos', '--data', str(data), *arguments],
            capture_output=True, text=True, encoding='utf-8',
        )
        print(result.stdout + result.stderr, end='', flush=True)
        if result.returncode != expected:
            raise RuntimeError(f'Ожидался код {expected}, получен {result.returncode}')
        return result.stdout + result.stderr

    run('add-student', 's1', 'Анна Иванова')
    run('add-student', 's2', 'Иван Петров')
    run('add-criterion', 'hw', 'Домашнее задание', '20', '0.4')
    run('add-criterion', 'exam', 'Экзамен', '10', '0.6')
    run('students')
    run('criteria')
    run('grade', 's1', 'hw', '21', expected=1)
    run('grade', 's1', 'unknown', '1', expected=1)
    run('grade', 's1', 'hw', '16', '--comment', 'Хорошая работа')
    run('grade', 's1', 'exam', '9')
    run('grade', 's2', 'hw', '10')
    if '8.60' not in run('total', 's1'):
        raise RuntimeError('Неверный итог исходного примера')
    run('revise', 's1', 'exam', '10')
    if '9.20' not in run('total', 's1'):
        raise RuntimeError('Итог не обновился после исправления')
    run('revoke', 's1', 'exam')
    if '3.20' not in run('total', 's1'):
        raise RuntimeError('Итог не обновился после отмены')
    run('--policy', 'strict', 'report')
    run('grade', 's1', 'exam', '9')
    run('scores', 's1')
    run('report')
    run('export', str(report))
    with report.open(encoding='utf-8-sig', newline='') as stream:
        rows = list(csv.DictReader(stream, delimiter=';'))
    if len(rows) != 2 or rows[0]['final_score'] != '8.60':
        raise RuntimeError('Неверное содержимое CSV')
    print(f'\nДемонстрация пройдена. Данные и CSV: {directory}')


if __name__ == '__main__':
    main()
