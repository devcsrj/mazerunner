//
// Copyright © 2018 Reijhanniel Jearl Campos (devcsrj@torocloud.com)
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//     http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.
//

package main

import (
	"github.com/golang-collections/collections/stack"
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

var (
	NORTH = 1
	EAST  = 2
	SOUTH = 3
	WEST  = 4
) // Value is important

type Pledge struct{}

func (s Pledge) solve(r Runner, goal Point) {

	prev := r.jump()
	current := prev
	for current.point != goal {
		directions := s.relativeDirection(prev.point, current)

		dir := WEST // Always take left
		var next *Point
		for next == nil {
			next = directions[dir]
			dir = s.rotate(dir, 1)
		}
		prev = current
		current = r.moveTo(*next)
	}
	r.celebrate()
}

func (s Pledge) relativeDirection(prev Point, position Position) map[int]*Point {
	absolute := s.absoluteDirection(position)
	if prev == position.point {
		return absolute
	}

	relative := make(map[int]*Point)
	rotation := 0
	for key, value := range absolute {
		if *value == prev {
			rotation = key + 4 - SOUTH
			break
		}
	}
	relative[SOUTH] = absolute[s.rotate(SOUTH, rotation)]
	relative[EAST] = absolute[s.rotate(EAST, rotation)]
	relative[NORTH] = absolute[s.rotate(NORTH, rotation)]
	relative[WEST] = absolute[s.rotate(WEST, rotation)]
	return relative
}

// Rotates the provided direction according to the order: NWSE
func (s Pledge) rotate(direction int, rotation int) int {
	i := direction + rotation
	for i > 4 {
		i -= 4
	}
	return i
}

// A map of the absoluteDirection and associated point
func (s Pledge) absoluteDirection(position Position) map[int]*Point {
	dir := make(map[int]*Point)
	here := position.point
	for _, neighbor := range position.neighbors {
		pt := neighbor
		if neighbor.x-here.x > 0 {
			dir[EAST] = &pt
		}
		if neighbor.x-here.x < 0 {
			dir[WEST] = &pt
		}
		if neighbor.y-here.y > 0 {
			dir[SOUTH] = &pt
		}
		if neighbor.y-here.y < 0 {
			dir[NORTH] = &pt
		}
	}
	return dir
}

type Dfs struct{}

func (s Dfs) solve(r Runner, goal Point) {

	type node struct {
		point     Point
		neighbors []Point
		parent    *node
	}

	explored := make(map[Point]bool)
	current := r.jump()

	routes := stack.New()
	routes.Push(node{
		point:     current.point,
		neighbors: current.neighbors,
		parent:    nil,
	})
	for routes.Len() != 0 {

		current := routes.Pop().(node)
		explored[current.point] = true
		if current.point == goal {
			r.celebrate()
			break
		}

		for _, neighbor := range current.neighbors {
			if explored[neighbor] {
				continue
			}

			next := r.moveTo(neighbor)
			routes.Push(node{
				point:     next.point,
				neighbors: next.neighbors,
				parent:    &current,
			})
			break
		}

		if routes.Len() == 0 {
			r.moveTo(current.parent.point) // dead end, let's back track
			routes.Push(*current.parent)
		}
	}
}
