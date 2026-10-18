package fleet

import (
	"errors"
	"fmt"
)

const BeginnerScoreThreshold = 6

var (
	ErrDuplicateInventoryNumber  = errors.New("inventory number already exists")
	ErrVehicleRequiresInspection = errors.New("vehicle must be added through AddVehicle")
)

type Fleet struct {
	serviceCenter ServiceCenter
	items         []InventoryItem
}

func NewFleet(serviceCenter ServiceCenter) *Fleet {
	return &Fleet{serviceCenter: serviceCenter}
}

func (f *Fleet) AddVehicle(vehicle Vehicle) error {
	if f.hasInventoryNumber(vehicle.InventoryNumber()) {
		return fmt.Errorf(
			"%w: %s",
			ErrDuplicateInventoryNumber,
			vehicle.InventoryNumber(),
		)
	}

	if err := f.serviceCenter.Inspect(vehicle); err != nil {
		return fmt.Errorf(
			"техосмотр транспорта %s: %w",
			vehicle.InventoryNumber(),
			err,
		)
	}

	f.items = append(f.items, vehicle)
	return nil
}

func (f *Fleet) AddItem(item InventoryItem) error {
	if _, isVehicle := item.(Vehicle); isVehicle {
		return ErrVehicleRequiresInspection
	}
	if f.hasInventoryNumber(item.InventoryNumber()) {
		return fmt.Errorf(
			"%w: %s",
			ErrDuplicateInventoryNumber,
			item.InventoryNumber(),
		)
	}

	f.items = append(f.items, item)
	return nil
}

func (f *Fleet) AssetCount() int {
	return len(f.items)
}

func (f *Fleet) VehicleCount() int {
	count := 0
	for _, item := range f.items {
		if _, isVehicle := item.(Vehicle); isVehicle {
			count++
		}
	}

	return count
}

func (f *Fleet) TotalDailyEnergy() float64 {
	var total float64
	for _, item := range f.items {
		consumer, usesEnergy := item.(EnergyConsumer)
		if usesEnergy {
			total += consumer.DailyEnergyKWh()
		}
	}

	return total
}

func (f *Fleet) BeginnerVehicles() []Vehicle {
	var vehicles []Vehicle
	for _, item := range f.items {
		vehicle, isVehicle := item.(Vehicle)
		if isVehicle && vehicle.BeginnerScore() >= BeginnerScoreThreshold {
			vehicles = append(vehicles, vehicle)
		}
	}

	return vehicles
}

func (f *Fleet) Inventory() []InventoryItem {
	items := make([]InventoryItem, len(f.items))
	copy(items, f.items)
	return items
}

func (f *Fleet) hasInventoryNumber(number InventoryNumber) bool {
	for _, item := range f.items {
		if item.InventoryNumber() == number {
			return true
		}
	}

	return false
}
