package fleet

import (
	"errors"
	"strings"
)

var ErrEmptyInventoryNumber = errors.New("inventory number cannot be empty")

type InventoryNumber string

func NewInventoryNumber(value string) (InventoryNumber, error) {
	value = strings.TrimSpace(value)
	if value == "" {
		return "", ErrEmptyInventoryNumber
	}

	return InventoryNumber(value), nil
}

func (n InventoryNumber) String() string {
	return string(n)
}
