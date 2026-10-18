package fleet

import (
	"errors"
	"math"
	"testing"
)

func TestFleetBuildsReportsForVehiclesAndThings(t *testing.T) {
	fleet := NewFleet(&serviceCenterStub{})

	scooter := newTestScooter(t)
	bicycle, err := NewBicycle("City bicycle", mustInventoryNumber(t, "B-001"), 5, 75)
	if err != nil {
		t.Fatalf("failed to create bicycle: %v", err)
	}
	electricBike, err := NewElectricBike(
		"Electric bike",
		mustInventoryNumber(t, "E-001"),
		8,
		0.8,
		90,
	)
	if err != nil {
		t.Fatalf("failed to create electric bike: %v", err)
	}
	helmet, err := NewHelmet("Helmet", mustInventoryNumber(t, "H-001"))
	if err != nil {
		t.Fatalf("failed to create helmet: %v", err)
	}
	station, err := NewChargingStation(
		"Charging station",
		mustInventoryNumber(t, "C-001"),
		2.7,
	)
	if err != nil {
		t.Fatalf("failed to create charging station: %v", err)
	}

	for _, vehicle := range []Vehicle{scooter, bicycle, electricBike} {
		if err := fleet.AddVehicle(vehicle); err != nil {
			t.Fatalf("failed to add vehicle: %v", err)
		}
	}
	for _, item := range []InventoryItem{helmet, station} {
		if err := fleet.AddItem(item); err != nil {
			t.Fatalf("failed to add item: %v", err)
		}
	}

	if fleet.AssetCount() != 5 {
		t.Fatalf("expected 5 assets, got %d", fleet.AssetCount())
	}
	if fleet.VehicleCount() != 3 {
		t.Fatalf("expected 3 vehicles, got %d", fleet.VehicleCount())
	}
	if math.Abs(fleet.TotalDailyEnergy()-5.0) > 0.000001 {
		t.Fatalf("expected 5.0 kWh, got %.2f", fleet.TotalDailyEnergy())
	}

	beginnerVehicles := fleet.BeginnerVehicles()
	if len(beginnerVehicles) != 2 {
		t.Fatalf("expected 2 beginner vehicles, got %d", len(beginnerVehicles))
	}

	inventory := fleet.Inventory()
	if len(inventory) != 5 {
		t.Fatalf("expected 5 inventory items, got %d", len(inventory))
	}
}

func TestFleetRejectsDuplicateInventoryNumber(t *testing.T) {
	fleet := NewFleet(&serviceCenterStub{})
	scooter := newTestScooter(t)
	helmet, err := NewHelmet("Helmet", scooter.InventoryNumber())
	if err != nil {
		t.Fatalf("failed to create helmet: %v", err)
	}

	if err := fleet.AddVehicle(scooter); err != nil {
		t.Fatalf("failed to add vehicle: %v", err)
	}
	err = fleet.AddItem(helmet)

	if !errors.Is(err, ErrDuplicateInventoryNumber) {
		t.Fatalf("expected duplicate inventory error, got %v", err)
	}
	if fleet.AssetCount() != 1 {
		t.Fatalf("expected one asset, got %d", fleet.AssetCount())
	}
}

func TestFleetDoesNotAllowVehicleToBypassInspection(t *testing.T) {
	serviceCenter := &serviceCenterStub{}
	fleet := NewFleet(serviceCenter)

	err := fleet.AddItem(newTestScooter(t))

	if !errors.Is(err, ErrVehicleRequiresInspection) {
		t.Fatalf("expected inspection requirement error, got %v", err)
	}
	if serviceCenter.calls != 0 {
		t.Fatalf("expected no inspection calls, got %d", serviceCenter.calls)
	}
	if fleet.AssetCount() != 0 {
		t.Fatalf("expected an empty fleet, got %d assets", fleet.AssetCount())
	}
}

func TestBeginnerVehiclesUsesScoreThreshold(t *testing.T) {
	fleet := NewFleet(&serviceCenterStub{})
	notForBeginner, err := NewBicycle(
		"Advanced bicycle",
		mustInventoryNumber(t, "B-005"),
		5,
		80,
	)
	if err != nil {
		t.Fatalf("failed to create bicycle: %v", err)
	}
	forBeginner, err := NewBicycle(
		"Beginner bicycle",
		mustInventoryNumber(t, "B-006"),
		6,
		80,
	)
	if err != nil {
		t.Fatalf("failed to create bicycle: %v", err)
	}

	if err := fleet.AddVehicle(notForBeginner); err != nil {
		t.Fatalf("failed to add bicycle: %v", err)
	}
	if err := fleet.AddVehicle(forBeginner); err != nil {
		t.Fatalf("failed to add bicycle: %v", err)
	}

	vehicles := fleet.BeginnerVehicles()
	if len(vehicles) != 1 {
		t.Fatalf("expected one beginner vehicle, got %d", len(vehicles))
	}
	if vehicles[0].InventoryNumber() != forBeginner.InventoryNumber() {
		t.Fatalf("unexpected beginner vehicle: %s", vehicles[0].InventoryNumber())
	}
}

func mustInventoryNumber(t *testing.T, value string) InventoryNumber {
	t.Helper()

	number, err := NewInventoryNumber(value)
	if err != nil {
		t.Fatalf("failed to create inventory number: %v", err)
	}

	return number
}
