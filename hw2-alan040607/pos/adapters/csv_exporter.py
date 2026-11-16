import csv
import os
import tempfile
from contextlib import suppress
from decimal import Decimal, ROUND_HALF_UP
from pathlib import Path

from pos.application.ports import ReportRow, StorageError


def display_score(value: Decimal | None) -> str:
    return '' if value is None else str(value.quantize(Decimal('0.01'),
                                                    rounding=ROUND_HALF_UP))


def spreadsheet_text(value: str) -> str:
    # CSV quoting alone does not prevent spreadsheet formula interpretation.
    return "'" + value if value.lstrip().startswith(('=', '+', '-', '@')) else value


class CsvReportExporter:
    def __init__(self, protected_path: str | os.PathLike[str] | None = None) -> None:
        self._protected_path = Path(protected_path) if protected_path else None

    def export(self, rows: tuple[ReportRow, ...], destination: str) -> None:
        temporary: Path | None = None
        try:
            path = Path(destination)
            if self._protected_path is not None:
                protected = self._protected_path.resolve()
                if protected == path.resolve() or (
                    protected.exists() and path.exists() and protected.samefile(path)
                ):
                    raise StorageError('Нельзя экспортировать поверх файла хранилища.')
            path.parent.mkdir(parents=True, exist_ok=True)
            with tempfile.NamedTemporaryFile(mode='w', encoding='utf-8-sig', newline='',
                                             dir=path.parent, delete=False) as stream:
                temporary = Path(stream.name)
                writer = csv.writer(stream, delimiter=';')
                writer.writerow(('student_id', 'name', 'final_score', 'status', 'missing_count'))
                for row in rows:
                    writer.writerow((spreadsheet_text(row.student_id), spreadsheet_text(row.name),
                                     display_score(row.result.value), row.result.status,
                                     row.result.missing_count))
                stream.flush()
                os.fsync(stream.fileno())
            os.replace(temporary, path)
        except (OSError, ValueError) as error:
            raise StorageError(f'Не удалось экспортировать ведомость: {error}') from error
        finally:
            if temporary is not None:
                # Cleanup must not hide the original storage error.
                with suppress(OSError):
                    temporary.unlink(missing_ok=True)
