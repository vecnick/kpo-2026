import json
import os
import tempfile
from contextlib import suppress
from pathlib import Path

from pos.application.ports import StorageError
from pos.domain.models import Gradebook

from .serialization import from_document, to_document


class JsonGradebookRepository:
    """Single-process storage; atomic replace prevents partial JSON files."""

    def __init__(self, path: str | os.PathLike[str]) -> None:
        self.path = Path(path)

    def load(self) -> Gradebook:
        try:
            with self.path.open(encoding='utf-8') as stream:
                return from_document(json.load(stream))
        except FileNotFoundError:
            return Gradebook()
        except (OSError, ValueError, TypeError, KeyError, UnicodeError) as error:
            raise StorageError(f"Не удалось прочитать {self.path}: {error}") from error

    def save(self, book: Gradebook) -> None:
        temporary: Path | None = None
        try:
            self.path.parent.mkdir(parents=True, exist_ok=True)
            with tempfile.NamedTemporaryFile(mode='w', encoding='utf-8',
                                             dir=self.path.parent, delete=False) as stream:
                temporary = Path(stream.name)
                json.dump(to_document(book), stream, ensure_ascii=False, indent=2)
                stream.flush()
                os.fsync(stream.fileno())
            os.replace(temporary, self.path)
        except (OSError, ValueError) as error:
            raise StorageError(f"Не удалось сохранить {self.path}: {error}") from error
        finally:
            if temporary is not None:
                with suppress(OSError):
                    temporary.unlink(missing_ok=True)


class InMemoryGradebookRepository:
    """Detached snapshots: failed use cases cannot mutate stored state."""

    def __init__(self) -> None:
        self._document = to_document(Gradebook())

    def load(self) -> Gradebook:
        return from_document(self._document)

    def save(self, book: Gradebook) -> None:
        self._document = to_document(book)
