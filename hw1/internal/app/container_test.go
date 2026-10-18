package app

import (
	"testing"

	"github.com/dushparra/kpo-2026/hw1/internal/fleet"
)

type serviceCenterStub struct {
	calls int
}

func (s *serviceCenterStub) Inspect(fleet.Vehicle) error {
	s.calls++
	return nil
}

func TestContainerUsesProvidedServiceCenter(t *testing.T) {
	serviceCenter := &serviceCenterStub{}
	container := NewContainer(serviceCenter)
	number, err := fleet.NewInventoryNumber("S-001")
	if err != nil {
		t.Fatalf("failed to create inventory number: %v", err)
	}
	scooter, err := fleet.NewScooter("Test scooter", number, 7, 1, 80)
	if err != nil {
		t.Fatalf("failed to create scooter: %v", err)
	}

	if err := container.Fleet.AddVehicle(scooter); err != nil {
		t.Fatalf("failed to add scooter: %v", err)
	}
	if serviceCenter.calls != 1 {
		t.Fatalf("expected one inspection, got %d", serviceCenter.calls)
	}
}
