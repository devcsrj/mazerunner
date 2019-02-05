package main

import (
	"encoding/json"
	"flag"
	"fmt"
	"github.com/gorilla/websocket"
	"log"
	"net/http"
	"net/url"
	"os"
	"os/signal"
	"strconv"
	"strings"
)

var addr = flag.String("addr", "localhost:8999", "address")
var id = flag.String("id", "ninja", "The runner id")
var name = flag.String("name", "TOGO", "The runner name")
var h = flag.String("heuristic", "manhattan", "Heuristics to use [fixed, manhattan, diagonal, euclidian]")

func main() {
	flag.Parse()

	interrupt := make(chan os.Signal, 1)
	signal.Notify(interrupt, os.Interrupt, os.Kill)

	u := url.URL{Scheme: "ws", Host: *addr, Path: "/maze/move"}
	log.Printf("connecting to %s", u.String())

	conn, _, err := websocket.DefaultDialer.Dial(u.String(), http.Header{
		"x-runner-tag": []string{*id + ":" + *name},
	})
	if err != nil {
		log.Fatal("dial:", err)
	}
	defer conn.Close()

	done := make(chan struct{})
	go func() {
		defer close(done)
		solve(*conn, parseHeuristic(*h))
	}()

	for {
		select {
		case <-done:
			return
		case <-interrupt:
			log.Println("interrupt")

			// Cleanly close the connection by sending a close message and then
			// waiting (with timeout) for the server to close the connection.
			err := conn.WriteMessage(
				websocket.CloseMessage,
				websocket.FormatCloseMessage(websocket.CloseNormalClosure, ""))
			if err != nil {
				log.Println("write close:", err)
			}
			return
		}
	}
}

// Solves the maze using a*
func solve(conn websocket.Conn, alg heuristic) {
	goal := goal()
	frontier := make(PriorityQueue, 0)
	cameFrom := make(map[point]position)
	costSoFar := make(map[point]int)

	// Put starting node to open
	position := whereAmI(conn)
	frontier.Push(&node{cost: 0, point: position.point})
	costSoFar[position.point] = 0

	for frontier.Len() != 0 {

		next := frontier.Pop().(*node)
		if next.point == goal {
			fmt.Println("I won!")
			break // we're done
		}

		if position.IsAdjacent(next.point) {
			position = moveTo(next.point, conn)
		} else {
			// don't just jump
			position = retrace(position.point, next.point, cameFrom, conn)
		}

		for _, neighbor := range position.neighbors {
			newCost := costSoFar[next.point] + movementCost(next.point, neighbor)
			if currentCost, existing := costSoFar[neighbor]; !existing || newCost < currentCost {
				costSoFar[neighbor] = newCost
				h := int(alg.compute(neighbor, goal))
				frontier.Push(&node{cost: newCost + h, point: neighbor})

				cameFrom[neighbor] = *position
				//cameFrom[neighbor] = next.point
				//cameFrom.PushFront(next.point)
			}
		}
	}
}

// Moves from given point to destination. Retraces steps based from route, if any.
func retrace(src point, dest point, routes map[point]position, conn websocket.Conn) *position {

	backtrack := make([]point, 0)
	last := src
	for {
		if position, existing := routes[last]; existing {
			last = position.point
			backtrack = append(backtrack, last)

			if position.IsAdjacent(dest) {
				break
			}
		}
	}
	backtrack = append(backtrack, dest)

	var pos *position
	for _, step := range backtrack {
		pos = moveTo(step, conn)
	}
	return pos
}

func movementCost(src point, dest point) int {
	if src == dest {
		return 0
	}
	return 1
}

func goal() point {
	u := url.URL{Scheme: "http", Host: *addr, Path: "/maze/goal"}
	resp, err := http.Get(u.String())
	if err != nil {
		panic("Could not determine goal from: " + u.String())
	}
	defer resp.Body.Close()
	d := json.NewDecoder(resp.Body)
	d.UseNumber()

	m := make(map[string]int)
	err = d.Decode(&m)
	if err != nil {
		panic("Could not parse goal from: " + u.String())
	}
	return point{m["x"], m["y"]}
}

func whereAmI(conn websocket.Conn) *position {
	return moveTo(point{-1, -1}, conn)
}

// Sends point as to connection in the form of: (x,y)
// Then constructs a position based from a response payload of: (10,10)[(10,9),(9,10)]
func moveTo(pt point, conn websocket.Conn) *position {
	payload := fmt.Sprintf("(%d,%d)", pt.x, pt.y)
	err := conn.WriteMessage(websocket.TextMessage, []byte(payload))
	if err != nil {
		log.Println("write close:", err)
	}

	_, message, err := conn.ReadMessage()
	if err != nil {
		panic(err)
	}
	log.Printf("recv: %s", message)

	// Receive
	points := make([]point, 0)
	buffer := make([]byte, 0)
	for i := 0; i < len(message); i++ {
		token := message[i]
		switch token {
		case byte('['), byte(']'):
		case byte(')'):
			buffer = append(buffer, token) // (x,y)
			points = append(points, pointFrom(string(buffer)))
			buffer = buffer[:0] // clear
		case byte(','):
			if len(buffer) == 0 {
				break // don't append
			}
			fallthrough
		default:
			buffer = append(buffer, token)
		}
	}
	return &position{points[0], points[1:]}
}

// Constructs a point based from a string of: (x,y)
func pointFrom(str string) point {
	tokens := strings.Split(str[1:len(str)-1], ",")
	x, _ := strconv.Atoi(tokens[0])
	y, _ := strconv.Atoi(tokens[1])
	return point{x, y}
}

func parseHeuristic(str string) heuristic {
	switch str {
	case "fixed":
		return fixed{1}
	case "manhattan":
		return manhattan{}
	case "diagonal":
		return diagonal{}
	case "euclidian":
		return euclidian{}
	default:
		panic("Unknown heuristics: " + str)
	}
}
