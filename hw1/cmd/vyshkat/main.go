package main

import (
	"log"
	"os"

	"github.com/dushparra/kpo-2026/hw1/internal/app"
	"github.com/dushparra/kpo-2026/hw1/internal/console"
)

func main() {
	container := app.NewDefaultContainer()
	if err := console.Run(container.Fleet, os.Stdin, os.Stdout); err != nil {
		log.Fatal(err)
	}
}
