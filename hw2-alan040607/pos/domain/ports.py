from typing import Protocol

from .models import Gradebook


class GradebookRepository(Protocol):
    def load(self) -> Gradebook: ...
    def save(self, book: Gradebook) -> None: ...
