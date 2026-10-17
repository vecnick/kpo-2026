"""Запуск интерактивного приложения: python -m vyshkat."""

import argparse

from vyshkat.container import Container


def main(argv: list[str] | None = None) -> None:
    parser = argparse.ArgumentParser(description="ВышКат — учёт парка и вещей")
    parser.parse_args(argv)
    app = Container().console()
    app.run()


if __name__ == "__main__":
    main()
