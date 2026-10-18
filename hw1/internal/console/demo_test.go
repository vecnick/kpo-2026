package console_test

import (
	"bytes"
	"strings"
	"testing"

	"github.com/dushparra/kpo-2026/hw1/internal/app"
	"github.com/dushparra/kpo-2026/hw1/internal/console"
)

func TestRunDemonstratesApplicationScenarios(t *testing.T) {
	container := app.NewDefaultContainer()
	input := strings.NewReader(strings.Join([]string{
		"1", "Самокат", "S-001", "8", "1.5", "85",
		"2", "Велосипед", "B-001", "5", "75",
		"3", "Электровелосипед", "E-001", "7", "0.9", "90",
		"1", "Неисправный самокат", "S-002", "7", "1.2", "30",
		"4", "Шлем", "H-001",
		"5", "Зарядная станция", "C-001", "2.4",
		"6",
		"0",
	}, "\n"))
	var output bytes.Buffer

	if err := console.Run(container.Fleet, input, &output); err != nil {
		t.Fatalf("failed to run console demo: %v", err)
	}

	expectedFragments := []string{
		"Принят: Самокат [S-001]",
		"Отклонён: Неисправный самокат [S-002]",
		"Транспорт: 3",
		"Объектов на балансе: 5",
		"Суточное энергопотребление: 4.8 кВт·ч",
		"Подходит новичкам:",
		"Инвентарь:",
		"Добавлено на баланс: Шлем [H-001]",
		"Добавлено на баланс: Зарядная станция [C-001]",
		"Шлем [H-001]",
		"Зарядная станция [C-001]",
	}

	for _, fragment := range expectedFragments {
		if !strings.Contains(output.String(), fragment) {
			t.Errorf("output does not contain %q\nfull output:\n%s", fragment, output.String())
		}
	}
}
