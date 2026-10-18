package fleet

import (
	"errors"
	"testing"
)

type serviceCenterStub struct {
	inspectionError error
	calls           int
}

func (s *serviceCenterStub) Inspect(Vehicle) error {
	s.calls++
	return s.inspectionError
}

func TestFleetAddsVehicleAfterSuccessfulInspection(t *testing.T) {
	serviceCenter := &serviceCenterStub{}
	fleet := NewFleet(serviceCenter)
	scooter := newTestScooter(t)

	err := fleet.AddVehicle(scooter)

	if err != nil {
		t.Fatalf("expected vehicle to be accepted, got error: %v", err)
	}
	if fleet.VehicleCount() != 1 {
		t.Fatalf("expected one vehicle in fleet, got %d", fleet.VehicleCount())
	}
	if serviceCenter.calls != 1 {
		t.Fatalf("expected one inspection, got %d", serviceCenter.calls)
	}
}

func TestFleetDoesNotAddVehicleAfterFailedInspection(t *testing.T) {
	inspectionError := errors.New("inspection failed")
	serviceCenter := &serviceCenterStub{inspectionError: inspectionError}
	fleet := NewFleet(serviceCenter)
	scooter := newTestScooter(t)

	err := fleet.AddVehicle(scooter)

	if !errors.Is(err, inspectionError) {
		t.Fatalf("expected inspection error, got %v", err)
	}
	if fleet.VehicleCount() != 0 {
		t.Fatalf("expected an empty fleet, got %d vehicles", fleet.VehicleCount())
	}
	if serviceCenter.calls != 1 {
		t.Fatalf("expected one inspection, got %d", serviceCenter.calls)
	}
}

func newTestScooter(t *testing.T) *Scooter {
	t.Helper()

	inventoryNumber, err := NewInventoryNumber("S-001")
	if err != nil {
		t.Fatalf("failed to create inventory number: %v", err)
	}

	scooter, err := NewScooter("Test scooter", inventoryNumber, 7, 1.5, 80)
	if err != nil {
		t.Fatalf("failed to create scooter: %v", err)
	}

	return scooter
}
