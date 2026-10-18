package fleet

import (
	"errors"
	"strings"
)

var ErrEmptyThingName = errors.New("thing name cannot be empty")

type Helmet struct {
	name            string
	inventoryNumber InventoryNumber
}

func NewHelmet(name string, inventoryNumber InventoryNumber) (*Helmet, error) {
	name, err := validateThing(name, inventoryNumber)
	if err != nil {
		return nil, err
	}

	return &Helmet{
		name:            name,
		inventoryNumber: inventoryNumber,
	}, nil
}

func (h *Helmet) Name() string {
	return h.name
}

func (h *Helmet) InventoryNumber() InventoryNumber {
	return h.inventoryNumber
}

type ChargingStation struct {
	name            string
	inventoryNumber InventoryNumber
	dailyEnergyKWh  float64
}

func NewChargingStation(
	name string,
	inventoryNumber InventoryNumber,
	dailyEnergyKWh float64,
) (*ChargingStation, error) {
	name, err := validateThing(name, inventoryNumber)
	if err != nil {
		return nil, err
	}
	if dailyEnergyKWh < 0 {
		return nil, ErrInvalidDailyEnergy
	}

	return &ChargingStation{
		name:            name,
		inventoryNumber: inventoryNumber,
		dailyEnergyKWh:  dailyEnergyKWh,
	}, nil
}

func (s *ChargingStation) Name() string {
	return s.name
}

func (s *ChargingStation) InventoryNumber() InventoryNumber {
	return s.inventoryNumber
}

func (s *ChargingStation) DailyEnergyKWh() float64 {
	return s.dailyEnergyKWh
}

func validateThing(name string, inventoryNumber InventoryNumber) (string, error) {
	name = strings.TrimSpace(name)
	if name == "" {
		return "", ErrEmptyThingName
	}
	if strings.TrimSpace(inventoryNumber.String()) == "" {
		return "", ErrEmptyInventoryNumber
	}

	return name, nil
}
