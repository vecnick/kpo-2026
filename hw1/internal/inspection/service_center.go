package inspection

import (
	"errors"
	"fmt"

	"github.com/dushparra/kpo-2026/hw1/internal/fleet"
)

const MinimumTechnicalCondition = 60

var ErrVehicleFailedInspection = errors.New("транспорт не прошёл техосмотр")

type StandardServiceCenter struct{}

func NewStandardServiceCenter() *StandardServiceCenter {
	return &StandardServiceCenter{}
}

func (s *StandardServiceCenter) Inspect(vehicle fleet.Vehicle) error {
	if vehicle.TechnicalCondition() < MinimumTechnicalCondition {
		return fmt.Errorf(
			"%w: %s, техническое состояние %d",
			ErrVehicleFailedInspection,
			vehicle.InventoryNumber(),
			vehicle.TechnicalCondition(),
		)
	}

	return nil
}
