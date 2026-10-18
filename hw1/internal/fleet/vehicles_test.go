package fleet

import (
	"errors"
	"testing"
)

func TestNewScooterValidatesFields(t *testing.T) {
	tests := []struct {
		name               string
		vehicleName        string
		inventoryNumber    InventoryNumber
		beginnerScore      int
		dailyEnergyKWh     float64
		technicalCondition int
		expectedError      error
	}{
		{
			name:               "empty name",
			vehicleName:        " ",
			inventoryNumber:    "S-001",
			beginnerScore:      7,
			dailyEnergyKWh:     1,
			technicalCondition: 80,
			expectedError:      ErrEmptyVehicleName,
		},
		{
			name:               "empty inventory number",
			vehicleName:        "Scooter",
			inventoryNumber:    "",
			beginnerScore:      7,
			dailyEnergyKWh:     1,
			technicalCondition: 80,
			expectedError:      ErrEmptyInventoryNumber,
		},
		{
			name:               "invalid beginner score",
			vehicleName:        "Scooter",
			inventoryNumber:    "S-001",
			beginnerScore:      11,
			dailyEnergyKWh:     1,
			technicalCondition: 80,
			expectedError:      ErrInvalidBeginnerScore,
		},
		{
			name:               "negative daily energy",
			vehicleName:        "Scooter",
			inventoryNumber:    "S-001",
			beginnerScore:      7,
			dailyEnergyKWh:     -1,
			technicalCondition: 80,
			expectedError:      ErrInvalidDailyEnergy,
		},
		{
			name:               "invalid technical condition",
			vehicleName:        "Scooter",
			inventoryNumber:    "S-001",
			beginnerScore:      7,
			dailyEnergyKWh:     1,
			technicalCondition: 101,
			expectedError:      ErrInvalidTechnicalCondition,
		},
	}

	for _, test := range tests {
		t.Run(test.name, func(t *testing.T) {
			_, err := NewScooter(
				test.vehicleName,
				test.inventoryNumber,
				test.beginnerScore,
				test.dailyEnergyKWh,
				test.technicalCondition,
			)

			if !errors.Is(err, test.expectedError) {
				t.Fatalf("expected %v, got %v", test.expectedError, err)
			}
		})
	}
}
