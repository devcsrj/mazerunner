#!/usr/bin/env bash

go run maze.go heuristic.go astar.go --id=$(date +%s) --name=kunoichi1 --heuristic=fixed \
&& go run maze.go heuristic.go astar.go --id=$(date +%s) --name=kunoichi2 --heuristic=random \
&& go run maze.go heuristic.go astar.go --id=$(date +%s) --name=kunoichi3 --heuristic=manhattan \
&& go run maze.go heuristic.go astar.go --id=$(date +%s) --name=kunoichi4 --heuristic=diagonal \
&& go run maze.go heuristic.go astar.go --id=$(date +%s) --name=kunoichi5 --heuristic=euclidian
