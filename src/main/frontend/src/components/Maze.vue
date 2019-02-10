<template>
  <v-stage ref="maze" :config="stageConfig">

  </v-stage>
</template>

<script>
	/* eslint-disable */
	import Vue from 'vue';
	import VueKonva from 'vue-konva'
	import VueResource from 'vue-resource';
	import VueNativeSock from 'vue-native-websocket'
	import Point from "./Point";
	import Dimension from "./Dimension";
	import Labyrinth from "./Labyrinth";
	import Movement from "./Movement";

	Vue.use(VueKonva);
	Vue.use(VueResource);
	Vue.use(VueNativeSock, '//' + window.location.host + "/maze/positions", {
		reconnect: true
	});

	const windowPad = 0;
	const width = window.innerWidth;
	const height = window.innerHeight;

	export default {
		name: "Maze",
		data: function () {
			return {
				stageConfig: {
					width: width - windowPad,
					height: height - windowPad
				}
			}
		},
		mounted: function () {
			const vm = this;
			const mazeRef = this.$refs.maze;

			// load maze info
			vm.$http.get("/maze/info").then(infoResponse => {
				return vm.$http.get("/maze/goal").then(goalResponse => {
					const info = infoResponse.body;
					vm.maze = new Labyrinth(info.columns, info.rows);

					const goal = new Point(goalResponse.body.x, goalResponse.body.y);
					vm.maze.init(mazeRef.getStage(), goal);
					vm.$options.sockets.onmessage = function (event) {
						const position = vm.parsePosition(event.data);
						vm.maze.update(position);
					};
				});
			});
		},
		methods: {
			/**
			 * Transforms the following string:
			 * ```
			 * [FAILED]id=devcsrj;name=RJ;
			 * [MOVED]id=devcsrj;name=RJ;position=(10,10)[(10,9),(9,10)]
			 * ```
			 * ...into a Position object
			 *
			 * @param data the raw string to parse
			 * @return the position object representing the string
			 */
			parsePosition: function (data) {
				const left = data.indexOf('[') + 1;
				const right = data.indexOf(']');
				const type = data.substring(left, right);

				// flatten into an object
				data = data.substring(type.length + 2);
				const map = {};
				const tokens = data.split(';');
				for (let i in tokens) {
					let pair = tokens[i].split('=');
					if (pair.length !== 2)
						continue;
					map[pair[0]] = pair[1];
				}
				let pt = null;
				if (map.position !== undefined) {
					let indexBeforeNeighbors = map.position.indexOf('[');
					let point = map.position.substring(1, indexBeforeNeighbors - 1);
					let pair = point.split(',');
					pt = new Point(Number(pair[0]), Number(pair[1]));
				}
				return new Movement(map.id, map.name, type, pt);
			}
		}
	}
</script>

<style scoped>

</style>
