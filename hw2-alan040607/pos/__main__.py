import sys
from collections.abc import Sequence

from pos.adapters.cli import execute, interactive, parser
from pos.application.ports import StorageError
from pos.application.services import PosApplicationService
from pos.bootstrap import build_container
from pos.domain.errors import DomainError


def main(argv: Sequence[str] | None = None) -> int:
    args = parser().parse_args(argv)
    app: PosApplicationService = build_container(args.data, args.policy).resolve(PosApplicationService)
    try:
        if args.command:
            execute(app, args)
        else:
            interactive(app)
    except (DomainError, StorageError) as error:
        print(f'Ошибка: {error}', file=sys.stderr)
        return 1
    except KeyboardInterrupt:
        print('\nРабота завершена.', file=sys.stderr)
        return 130
    return 0


if __name__ == '__main__':
    sys.exit(main())
