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
	"reflect"
)

var addr = flag.String("addr", "localhost:8999", "address")
var id = flag.String("id", "ninja", "The runner id")
var name = flag.String("name", "TOGO", "The runner name")
var alg = flag.String("alg", "dijkstra", "The algorithm to use ['dijkstra', 'headless', 'a*man', 'a*diag', 'a*euc']")
var verbose = flag.Bool("verbose", true, "Verbose")

var solutions = map[string]reflect.Type{
	"dijkstra": reflect.TypeOf(Dijkstra{}),
	"headless": reflect.TypeOf(Headless{}),
	"a*man":    reflect.TypeOf(AStarManhattan{}),
	"a*diag":   reflect.TypeOf(AStarDiagonal{}),
	"a*euc":    reflect.TypeOf(AStarEuclid{}),
}

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

	fmt.Printf("[%s] Solving maze with %s\n", *name, *alg)

	done := make(chan struct{})
	go func() {
		defer close(done)
		r := WsRunner{id: *id, name: *name, steps: 0, conn: conn}
		s := reflect.New(solutions[*alg]).Elem().Interface()
		s.(Solution).solve(&r, goal())
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

func goal() Point {
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
	return Point{x: m["x"], y: m["y"]}
}
