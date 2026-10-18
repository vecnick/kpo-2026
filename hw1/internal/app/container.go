package app

import (
	"github.com/dushparra/kpo-2026/hw1/internal/fleet"
	"github.com/dushparra/kpo-2026/hw1/internal/inspection"
)

type Container struct {
	Fleet *fleet.Fleet
}

func NewContainer(serviceCenter fleet.ServiceCenter) *Container {
	return &Container{
		Fleet: fleet.NewFleet(serviceCenter),
	}
}

func NewDefaultContainer() *Container {
	return NewContainer(inspection.NewStandardServiceCenter())
}
