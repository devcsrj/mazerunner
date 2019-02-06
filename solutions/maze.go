package main

type point struct {
	x int
	y int
}

type position struct {
	point     point
	neighbors []point
}

func (pos *position) IsAdjacent(point point) bool {
	if pos.point == point {
		return true
	}
	for _, n := range pos.neighbors {
		if n == point {
			return true
		}
	}
	return false
}

type node struct {
	cost  float64
	point point

	index int // The index of the item in the heap.
}
