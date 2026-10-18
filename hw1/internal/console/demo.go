package console

import (
	"bufio"
	"errors"
	"fmt"
	"io"
	"strconv"
	"strings"

	"github.com/dushparra/kpo-2026/hw1/internal/fleet"
)

type terminal struct {
	park    *fleet.Fleet
	scanner *bufio.Scanner
	output  io.Writer
}

func Run(park *fleet.Fleet, input io.Reader, output io.Writer) error {
	console := &terminal{
		park:    park,
		scanner: bufio.NewScanner(input),
		output:  output,
	}

	console.printMenu()
	for {
		command, err := console.readLine("\nВыберите действие: ")
		if errors.Is(err, io.EOF) {
			return nil
		}
		if err != nil {
			return err
		}

		var actionError error
		switch command {
		case "1":
			actionError = console.addScooter()
		case "2":
			actionError = console.addBicycle()
		case "3":
			actionError = console.addElectricBike()
		case "4":
			actionError = console.addHelmet()
		case "5":
			actionError = console.addChargingStation()
		case "6":
			console.printReport()
		case "help":
			console.printMenu()
		case "0":
			fmt.Fprintln(output, "Работа завершена.")
			return nil
		default:
			fmt.Fprintln(output, "Неизвестная команда. Введите help, чтобы увидеть меню.")
		}

		if errors.Is(actionError, io.EOF) {
			return nil
		}
		if actionError != nil {
			fmt.Fprintf(output, "Ошибка: %v\n", actionError)
		}
	}
}

func (c *terminal) printMenu() {
	fmt.Fprintln(c.output, "=== ВышКат ===")
	fmt.Fprintln(c.output, "1 - добавить самокат")
	fmt.Fprintln(c.output, "2 - добавить велосипед")
	fmt.Fprintln(c.output, "3 - добавить электровелосипед")
	fmt.Fprintln(c.output, "4 - добавить шлем")
	fmt.Fprintln(c.output, "5 - добавить зарядную станцию")
	fmt.Fprintln(c.output, "6 - показать отчёт")
	fmt.Fprintln(c.output, "0 - выйти")
}

func (c *terminal) addScooter() error {
	name, err := c.readLine("Название: ")
	if err != nil {
		return err
	}
	number, err := c.readInventoryNumber()
	if err != nil {
		return err
	}
	beginnerScore, err := c.readInt("Рейтинг для новичка (1-10): ")
	if err != nil {
		return err
	}
	dailyEnergy, err := c.readFloat("Энергия за сутки, кВт·ч: ")
	if err != nil {
		return err
	}
	condition, err := c.readInt("Техническое состояние (0-100): ")
	if err != nil {
		return err
	}

	scooter, err := fleet.NewScooter(
		name,
		number,
		beginnerScore,
		dailyEnergy,
		condition,
	)
	if err != nil {
		return err
	}

	c.addVehicle(scooter)
	return nil
}

func (c *terminal) addBicycle() error {
	name, err := c.readLine("Название: ")
	if err != nil {
		return err
	}
	number, err := c.readInventoryNumber()
	if err != nil {
		return err
	}
	beginnerScore, err := c.readInt("Рейтинг для новичка (1-10): ")
	if err != nil {
		return err
	}
	condition, err := c.readInt("Техническое состояние (0-100): ")
	if err != nil {
		return err
	}

	bicycle, err := fleet.NewBicycle(name, number, beginnerScore, condition)
	if err != nil {
		return err
	}

	c.addVehicle(bicycle)
	return nil
}

func (c *terminal) addElectricBike() error {
	name, err := c.readLine("Название: ")
	if err != nil {
		return err
	}
	number, err := c.readInventoryNumber()
	if err != nil {
		return err
	}
	beginnerScore, err := c.readInt("Рейтинг для новичка (1-10): ")
	if err != nil {
		return err
	}
	dailyEnergy, err := c.readFloat("Энергия за сутки, кВт·ч: ")
	if err != nil {
		return err
	}
	condition, err := c.readInt("Техническое состояние (0-100): ")
	if err != nil {
		return err
	}

	bicycle, err := fleet.NewElectricBike(
		name,
		number,
		beginnerScore,
		dailyEnergy,
		condition,
	)
	if err != nil {
		return err
	}

	c.addVehicle(bicycle)
	return nil
}

func (c *terminal) addHelmet() error {
	name, err := c.readLine("Название: ")
	if err != nil {
		return err
	}
	number, err := c.readInventoryNumber()
	if err != nil {
		return err
	}

	helmet, err := fleet.NewHelmet(name, number)
	if err != nil {
		return err
	}

	return c.addItem(helmet)
}

func (c *terminal) addChargingStation() error {
	name, err := c.readLine("Название: ")
	if err != nil {
		return err
	}
	number, err := c.readInventoryNumber()
	if err != nil {
		return err
	}
	dailyEnergy, err := c.readFloat("Энергия за сутки, кВт·ч: ")
	if err != nil {
		return err
	}

	station, err := fleet.NewChargingStation(name, number, dailyEnergy)
	if err != nil {
		return err
	}

	return c.addItem(station)
}

func (c *terminal) addVehicle(vehicle fleet.Vehicle) {
	if err := c.park.AddVehicle(vehicle); err != nil {
		fmt.Fprintf(
			c.output,
			"Отклонён: %s [%s] - %v\n",
			vehicle.Name(),
			vehicle.InventoryNumber(),
			err,
		)
		return
	}

	fmt.Fprintf(
		c.output,
		"Принят: %s [%s]\n",
		vehicle.Name(),
		vehicle.InventoryNumber(),
	)
}

func (c *terminal) addItem(item fleet.InventoryItem) error {
	if err := c.park.AddItem(item); err != nil {
		return err
	}

	fmt.Fprintf(
		c.output,
		"Добавлено на баланс: %s [%s]\n",
		item.Name(),
		item.InventoryNumber(),
	)
	return nil
}

func (c *terminal) printReport() {
	fmt.Fprintln(c.output, "\n=== Отчёт по парку ===")
	fmt.Fprintf(c.output, "Транспорт: %d\n", c.park.VehicleCount())
	fmt.Fprintf(c.output, "Объектов на балансе: %d\n", c.park.AssetCount())
	fmt.Fprintf(
		c.output,
		"Суточное энергопотребление: %.1f кВт·ч\n",
		c.park.TotalDailyEnergy(),
	)

	fmt.Fprintln(c.output, "\nПодходит новичкам:")
	for _, vehicle := range c.park.BeginnerVehicles() {
		fmt.Fprintf(
			c.output,
			"- %s [%s], рейтинг: %d\n",
			vehicle.Name(),
			vehicle.InventoryNumber(),
			vehicle.BeginnerScore(),
		)
	}

	fmt.Fprintln(c.output, "\nИнвентарь:")
	for _, item := range c.park.Inventory() {
		fmt.Fprintf(
			c.output,
			"- %s [%s]\n",
			item.Name(),
			item.InventoryNumber(),
		)
	}
}

func (c *terminal) readInventoryNumber() (fleet.InventoryNumber, error) {
	value, err := c.readLine("Инвентарный номер: ")
	if err != nil {
		return "", err
	}

	return fleet.NewInventoryNumber(value)
}

func (c *terminal) readInt(prompt string) (int, error) {
	value, err := c.readLine(prompt)
	if err != nil {
		return 0, err
	}

	number, err := strconv.Atoi(value)
	if err != nil {
		return 0, fmt.Errorf("неверное целое число %q: %w", value, err)
	}
	return number, nil
}

func (c *terminal) readFloat(prompt string) (float64, error) {
	value, err := c.readLine(prompt)
	if err != nil {
		return 0, err
	}

	number, err := strconv.ParseFloat(strings.Replace(value, ",", ".", 1), 64)
	if err != nil {
		return 0, fmt.Errorf("неверное число %q: %w", value, err)
	}
	return number, nil
}

func (c *terminal) readLine(prompt string) (string, error) {
	fmt.Fprint(c.output, prompt)
	if !c.scanner.Scan() {
		if err := c.scanner.Err(); err != nil {
			return "", err
		}
		return "", io.EOF
	}

	return strings.TrimSpace(c.scanner.Text()), nil
}
