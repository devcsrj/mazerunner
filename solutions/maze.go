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
	cost  int
	point point

	index int // The index of the item in the heap.
}

type PriorityQueue []*node

func (pq PriorityQueue) Len() int { return len(pq) }

func (pq PriorityQueue) Less(i, j int) bool {
	return pq[i].cost < pq[j].cost
}

func (pq *PriorityQueue) Pop() interface{} {
	old := *pq
	n := len(old)
	item := old[n-1]
	item.cost = -1
	*pq = old[0 : n-1]
	return item
}

func (pq *PriorityQueue) Push(x interface{}) {
	n := len(*pq)
	item := x.(*node)
	item.index = n
	*pq = append(*pq, item)
}

func (pq PriorityQueue) Swap(i, j int) {
	pq[i], pq[j] = pq[j], pq[i]
	pq[i].index = i
	pq[j].index = j
}
