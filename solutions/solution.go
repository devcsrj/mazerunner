package main

import (
	"sort"
)

type Key struct {
	k    int
	more interface{}
}

func key(k ...int) Key {
	var more interface{}
	if len(k) > 1 {
		more = key(k[1:]...)
	}
	return Key{k[0], more}
}

type SparseArray map[Key]*node

func (o SparseArray) get(point Point) *node {
	k := key(point.x, point.y)
	if o[k] == nil {
		o[k] = &node{point: point, visits: 0, distance: 0}
	}
	return o[k]
}

type Solution interface {
	solve(r Runner, goal Point)
}

type node struct {
	point    Point
	visits   int
	distance float64
}

type ByDistance []*node

func (a ByDistance) Len() int {
	return len(a)
}

func (a ByDistance) Less(i, j int) bool {
	// compare first by visit count
	result := a[i].visits - a[j].visits
	if result == 0 {
		// else, try by distance
		return a[i].distance < a[j].distance
	}
	return a[i].visits < a[j].visits
}

func (a ByDistance) Swap(i, j int) {
	a[i], a[j] = a[j], a[i]
}

type AStar struct{}

func (s AStar) solveWith(alg heuristic, r Runner, goal Point) {

	explored := make(SparseArray)
	current := r.jump()

	for current.point != goal {
		currentNode := explored.get(current.point)
		currentNode.visits++

		adjNodes := make([]*node, 0)
		for _, next := range current.neighbors {
			nextNode := explored.get(next)
			nextNode.distance = alg.compute(next, goal)
			adjNodes = append(adjNodes, nextNode)
		}
		sort.Sort(ByDistance(adjNodes))
		dest := adjNodes[0].point

		current = r.moveTo(dest)
	}
	r.celebrate()
}

type Dijkstra struct{}

func (s Dijkstra) solve(r Runner, goal Point) {
	a := AStar{}
	a.solveWith(fixed{0}, r, goal)
}

type Headless struct{}

func (s Headless) solve(r Runner, goal Point) {
	a := AStar{}
	a.solveWith(random{}, r, goal)
}

type AStarManhattan struct{}

func (s AStarManhattan) solve(r Runner, goal Point) {
	a := AStar{}
	a.solveWith(manhattan{}, r, goal)
}

type AStarDiagonal struct{}

func (s AStarDiagonal) solve(r Runner, goal Point) {
	a := AStar{}
	a.solveWith(diagonal{}, r, goal)
}

type AStarEuclid struct{}

func (s AStarEuclid) solve(r Runner, goal Point) {
	a := AStar{}
	a.solveWith(euclidian{}, r, goal)
}
