package main

import (
	"math"
	"math/rand"
)

type heuristic interface {
	compute(src point, dest point) float64
}

type fixed struct {
	value float64
}

func (h fixed) compute(src point, dest point) float64 {
	return h.value
}

type random struct {}

func (r random) compute(src point, dest point) float64 {
	return float64(rand.Intn(5))
}

type manhattan struct{}

func (h manhattan) compute(src point, dest point) float64 {
	x := float64(src.x) - float64(dest.x)
	y := float64(src.y) - float64(dest.y)
	return math.Abs(x) + math.Abs(y)
}

type diagonal struct{}

func (h diagonal) compute(src point, dest point) float64 {
	x := math.Abs(float64(src.x) - float64(dest.x))
	y := math.Abs(float64(src.y) - float64(dest.y))
	return math.Max(x, y)
}

type euclidian struct{}

func (h euclidian) compute(src point, dest point) float64 {
	x := math.Pow(float64(src.x)-float64(dest.x), 2)
	y := math.Pow(float64(src.y)-float64(dest.y), 2)
	return math.Sqrt(x + y)
}
