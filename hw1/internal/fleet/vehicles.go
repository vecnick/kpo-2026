package fleet

import (
	"errors"
	"strings"
)

var (
	ErrEmptyVehicleName          = errors.New("vehicle name cannot be empty")
	ErrInvalidBeginnerScore      = errors.New("beginner score must be between 1 and 10")
	ErrInvalidDailyEnergy        = errors.New("daily energy cannot be negative")
	ErrInvalidTechnicalCondition = errors.New("technical condition must be between 0 and 100")
)

type Scooter struct {
	name               string
	inventoryNumber    InventoryNumber
	beginnerScore      int
	dailyEnergyKWh     float64
	technicalCondition int
}

func NewScooter(
	name string,
	inventoryNumber InventoryNumber,
	beginnerScore int,
	dailyEnergyKWh float64,
	technicalCondition int,
) (*Scooter, error) {
	name, err := validateVehicle(
		name,
		inventoryNumber,
		beginnerScore,
		technicalCondition,
	)
	if err != nil {
		return nil, err
	}
	if dailyEnergyKWh < 0 {
		return nil, ErrInvalidDailyEnergy
	}

	return &Scooter{
		name:               name,
		inventoryNumber:    inventoryNumber,
		beginnerScore:      beginnerScore,
		dailyEnergyKWh:     dailyEnergyKWh,
		technicalCondition: technicalCondition,
	}, nil
}

func (s *Scooter) Name() string {
	return s.name
}

func (s *Scooter) InventoryNumber() InventoryNumber {
	return s.inventoryNumber
}

func (s *Scooter) BeginnerScore() int {
	return s.beginnerScore
}

func (s *Scooter) DailyEnergyKWh() float64 {
	return s.dailyEnergyKWh
}

func (s *Scooter) TechnicalCondition() int {
	return s.technicalCondition
}

type Bicycle struct {
	name               string
	inventoryNumber    InventoryNumber
	beginnerScore      int
	technicalCondition int
}

func NewBicycle(
	name string,
	inventoryNumber InventoryNumber,
	beginnerScore int,
	technicalCondition int,
) (*Bicycle, error) {
	name, err := validateVehicle(
		name,
		inventoryNumber,
		beginnerScore,
		technicalCondition,
	)
	if err != nil {
		return nil, err
	}

	return &Bicycle{
		name:               name,
		inventoryNumber:    inventoryNumber,
		beginnerScore:      beginnerScore,
		technicalCondition: technicalCondition,
	}, nil
}

func (b *Bicycle) Name() string {
	return b.name
}

func (b *Bicycle) InventoryNumber() InventoryNumber {
	return b.inventoryNumber
}

func (b *Bicycle) BeginnerScore() int {
	return b.beginnerScore
}

func (b *Bicycle) TechnicalCondition() int {
	return b.technicalCondition
}

type ElectricBike struct {
	name               string
	inventoryNumber    InventoryNumber
	beginnerScore      int
	dailyEnergyKWh     float64
	technicalCondition int
}

func NewElectricBike(
	name string,
	inventoryNumber InventoryNumber,
	beginnerScore int,
	dailyEnergyKWh float64,
	technicalCondition int,
) (*ElectricBike, error) {
	name, err := validateVehicle(
		name,
		inventoryNumber,
		beginnerScore,
		technicalCondition,
	)
	if err != nil {
		return nil, err
	}
	if dailyEnergyKWh < 0 {
		return nil, ErrInvalidDailyEnergy
	}

	return &ElectricBike{
		name:               name,
		inventoryNumber:    inventoryNumber,
		beginnerScore:      beginnerScore,
		dailyEnergyKWh:     dailyEnergyKWh,
		technicalCondition: technicalCondition,
	}, nil
}

func (b *ElectricBike) Name() string {
	return b.name
}

func (b *ElectricBike) InventoryNumber() InventoryNumber {
	return b.inventoryNumber
}

func (b *ElectricBike) BeginnerScore() int {
	return b.beginnerScore
}

func (b *ElectricBike) DailyEnergyKWh() float64 {
	return b.dailyEnergyKWh
}

func (b *ElectricBike) TechnicalCondition() int {
	return b.technicalCondition
}

func validateVehicle(
	name string,
	inventoryNumber InventoryNumber,
	beginnerScore int,
	technicalCondition int,
) (string, error) {
	name = strings.TrimSpace(name)
	if name == "" {
		return "", ErrEmptyVehicleName
	}
	if strings.TrimSpace(inventoryNumber.String()) == "" {
		return "", ErrEmptyInventoryNumber
	}
	if beginnerScore < 1 || beginnerScore > 10 {
		return "", ErrInvalidBeginnerScore
	}
	if technicalCondition < 0 || technicalCondition > 100 {
		return "", ErrInvalidTechnicalCondition
	}

	return name, nil
}
