import subprocess
import sys

import pytest

from vyshkat.__main__ import main
from vyshkat.cli import ConsoleApp


def run_dialog(fleet, answers):
    iterator = iter(answers)
    output = []
    app = ConsoleApp(fleet, read=lambda _: next(iterator), write=output.append)
    app.run()
    return "\n".join(output)


def test_menu_recovers_from_invalid_choice_and_shows_empty_reports(fleet):
    output = run_dialog(fleet, ["99", "3", "4", "5", "0"])
    assert "Нет такого пункта" in output
    assert "Транспорт: 0; вещи: 0" in output
    assert "Подходящего транспорта пока нет" in output
    assert "Баланс пуст" in output
    assert "До встречи" in output


@pytest.mark.parametrize("error", [EOFError, KeyboardInterrupt])
def test_end_of_input_is_graceful(fleet, error):
    def read(_):
        raise error

    output = []
    ConsoleApp(fleet, read=read, write=output.append).run()
    assert any("Ввод завершён" in line for line in output)


def test_dialog_adds_all_six_types_rejects_broken_vehicle_and_prints_reports(fleet):
    output = run_dialog(
        fleet,
        [
            "1",
            "1",
            "S",
            "Самокат",
            "6",
            "не знаю",
            "да",
            "2,5",
            "д",
            "1",
            "2",
            "B",
            "Вело",
            "5",
            "y",
            "1",
            "3",
            "E",
            "Электро",
            "8",
            "yes",
            "1.5",
            "д",
            "2",
            "1",
            "H",
            "Шлем",
            "2",
            "2",
            "D",
            "Док",
            "2",
            "3",
            "C",
            "Шкаф",
            "4",
            "1",
            "1",
            "BAD",
            "Сломанный",
            "9",
            "н",
            "3",
            "нет",
            "3",
            "4",
            "5",
            "0",
        ],
    )
    assert "Введите д (да) или н (нет)" in output
    assert "ОТКЛОНЕНО: Сломанный" in output
    assert "Транспорт: 3; вещи: 3; всего: 6" in output
    assert "Энергия транспорта: 4 кВт·ч/сутки" in output
    assert "Энергия всего баланса (включая вещи): 8" in output
    assert "Самокат | № S | простота 6/10" in output
    assert "Вело | № B | простота" not in output
    assert "Шкаф | № C" in output


def test_cancel_invalid_type_data_and_duplicate_keep_menu_alive(fleet):
    output = run_dialog(
        fleet,
        [
            "1",
            "0",
            "2",
            "99",
            "1",
            "2",
            "BAD",
            "Ошибка",
            "не число",
            "2",
            "3",
            "BAD",
            "Ошибка",
            "nan",
            "2",
            "1",
            "H",
            "Шлем",
            "2",
            "1",
            "H",
            "Дубликат",
            "0",
        ],
    )
    assert "Добавление отменено" in output
    assert "Неизвестный вид" in output
    assert "уже занят" in output
    assert output.count("Ошибка:") == 4
    assert fleet.report().item_count == 1


def test_non_numeric_energy_has_readable_error(fleet):
    output = run_dialog(fleet, ["2", "3", "C", "Шкаф", "abc", "0"])
    assert "Расход энергии должен быть числом, например 2,5" in output
    assert fleet.inventory() == ()


def test_main_interactive(monkeypatch):
    calls = []
    monkeypatch.setattr(ConsoleApp, "run", lambda self: calls.append(self))
    main([])
    assert len(calls) == 1


def test_module_entrypoint_accepts_console_input_in_real_process():
    answers = [
        "3",
        "5",
        "1",
        "1",
        "S-INPUT",
        "Самокат из ввода",
        "6",
        "д",
        "2,5",
        "д",
        "1",
        "3",
        "E-BAD",
        "Неисправный",
        "8",
        "д",
        "1",
        "н",
        "2",
        "1",
        "H-INPUT",
        "Шлем из ввода",
        "3",
        "4",
        "5",
        "0",
    ]
    result = subprocess.run(
        [sys.executable, "-X", "utf8", "-m", "vyshkat"],
        input="\n".join(answers) + "\n",
        capture_output=True,
        text=True,
        encoding="utf-8",
        check=False,
        timeout=20,
    )
    assert result.returncode == 0, result.stderr
    assert "Транспорт: 0; вещи: 0" in result.stdout
    assert "Баланс пуст" in result.stdout
    assert "ПРИНЯТО: Самокат из ввода [S-INPUT]" in result.stdout
    assert "ОТКЛОНЕНО: Неисправный [E-BAD]" in result.stdout
    assert "Транспорт: 1; вещи: 1; всего: 2" in result.stdout
    assert "Энергия транспорта: 2.5" in result.stdout
    assert "Самокат из ввода | № S-INPUT | простота 6/10" in result.stdout
    assert "Шлем из ввода | № H-INPUT" in result.stdout
    assert "До встречи" in result.stdout
