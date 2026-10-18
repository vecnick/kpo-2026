package fleet

type InventoryItem interface {
	Name() string
	InventoryNumber() InventoryNumber
}

type EnergyConsumer interface {
	DailyEnergyKWh() float64
}

type Vehicle interface {
	InventoryItem
	BeginnerScore() int
	TechnicalCondition() int
}

type ServiceCenter interface {
	Inspect(vehicle Vehicle) error
}
