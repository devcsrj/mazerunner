#!/usr/bin/env bash

go run maze.go heuristic.go exhaustive.go --id=$(date +%s) --name=kunoichi1 \
&& go run maze.go heuristic.go exhaustive.go --id=$(date +%s) --name=kunoichi2 \
&& go run maze.go heuristic.go exhaustive.go --id=$(date +%s) --name=kunoichi3 \
&& go run maze.go heuristic.go exhaustive.go --id=$(date +%s) --name=kunoichi4 \
&& go run maze.go heuristic.go exhaustive.go --id=$(date +%s) --name=kunoichi5
