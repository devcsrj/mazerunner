#!/usr/bin/env bash
#
# Copyright © 2018 Reijhanniel Jearl Campos (devcsrj@torocloud.com)
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
#


go run ./ --id=$(date +%s) --name="kunoichi1" --alg="headless" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi2" --alg="dijkstra" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi3" --alg="a*man" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi4" --alg="a*diag" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi5" --alg="a*euc" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi6" --alg="pledge" --verbose=false \
&& go run ./ --id=$(date +%s) --name="kunoichi7" --alg="dfs" --verbose=false \
