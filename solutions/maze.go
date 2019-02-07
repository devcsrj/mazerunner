package main

import (
	"bytes"
	"fmt"
	"github.com/gorilla/websocket"
	"log"
	"strconv"
	"strings"
)

type Point struct {
	x int
	y int
}

func (p Point) String() string {
	return fmt.Sprintf("(%d,%d)", p.x, p.y)
}

type Position struct {
	point     Point
	neighbors []Point
}

func (pos Position) String() string {
	buffer := bytes.Buffer{}
	buffer.WriteString(pos.point.String())
	buffer.WriteByte(byte('['))
	for _, neighbor := range pos.neighbors {
		buffer.WriteString(neighbor.String())
	}
	buffer.WriteByte(byte(']'))
	return buffer.String()
}

func (pos *Position) IsAdjacent(point Point) bool {
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

type Runner interface {
	// the current Position
	jump() Position

	// Sends Point as to connection in the form of: (x,y)
	// Then constructs a Position based from a response payload of: (10,10)[(10,9),(9,10)]
	moveTo(point Point) Position

	// Called when the Runner finishes the maze
	celebrate()
}

// A websocket implementation of Runner
type WsRunner struct {
	id   string
	name string

	steps int
	conn  *websocket.Conn
}

func (r WsRunner) jump() Position {
	return r.moveTo(Point{-1, -1})
}

func (r *WsRunner) moveTo(dest Point) Position {
	payload := fmt.Sprintf("(%d,%d)", dest.x, dest.y)
	err := r.conn.WriteMessage(websocket.TextMessage, []byte(payload))
	if err != nil {
		log.Println("write close:", err)
	}

	_, message, err := r.conn.ReadMessage()
	if err != nil {
		panic(err)
	}

	// Receive
	points := make([]Point, 0)
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
	r.steps = r.steps + 1 // healthy living
	pos := Position{points[0], points[1:]}

	if *verbose {
		fmt.Printf("[%s - %s] Moving to %v\n", r.id, r.name, pos)
	}
	return pos
}

func (r WsRunner) celebrate() {
	fmt.Printf("[%s - %s] Reached goal after %d move(s)\n", r.id, r.name, r.steps)
}

// Constructs a Point based from a string of: (x,y)
func pointFrom(str string) Point {
	tokens := strings.Split(str[1:len(str)-1], ",")
	x, _ := strconv.Atoi(tokens[0])
	y, _ := strconv.Atoi(tokens[1])
	return Point{x, y}
}
