package inspection

import (
	"errors"
	"testing"

	"github.com/dushparra/kpo-2026/hw1/internal/fleet"
)

func TestStandardServiceCenterChecksTechnicalCondition(t *testing.T) {
	tests := []struct {
		name      string
		condition int
		accepted  bool
	}{
		{name: "minimum condition is accepted", condition: 60, accepted: true},
		{name: "condition below minimum is rejected", condition: 59, accepted: false},
	}

	serviceCenter := NewStandardServiceCenter()
	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			number, err := fleet.NewInventoryNumber("B-001")
			if err != nil {
				t.Fatalf("failed to create inventory number: %v", err)
			}
			bicycle, err := fleet.NewBicycle("Test bicycle", number, 7, test.condition)
			if err != nil {
				t.Fatalf("failed to create bicycle: %v", err)
			}

			err = serviceCenter.Inspect(bicycle)

			if test.accepted && err != nil {
				t.Fatalf("expected vehicle to be accepted, got %v", err)
			}
			if !test.accepted && !errors.Is(err, ErrVehicleFailedInspection) {
				t.Fatalf("expected inspection error, got %v", err)
			}
		})
	}
}
