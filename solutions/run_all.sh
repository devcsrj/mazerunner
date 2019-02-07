#!/usr/bin/env bash

go run ./ --id=$(date +%s) --name="kunoichi1" --alg="headless" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi2" --alg="dijkstra" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi3" --alg="a*man" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi4" --alg="a*diag" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi5" --alg="a*euc" --verbose=false
