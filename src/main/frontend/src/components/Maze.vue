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

	Vue.use(VueKonva);
	Vue.use(VueResource);
	Vue.use(VueNativeSock, '//' + window.location.host + "/maze/positions", {
		reconnect: true
	});

	const windowPad = 4;
	const width = window.innerWidth;
	const height = window.innerHeight;

	class Point {

		/**
		 * @param x the x coordinate
		 * @param y the y coordinate
		 */
		constructor(x, y) {
			this.x = x;
			this.y = y;
		}

		toString() {
			return "(" + this.x + ", " + this.y + ")";
		}
	}

	class Dimension {

		constructor(height, width) {
			this.height = height;
			this.width = width;
		}
	}

	/**
	 * A typical room
	 */
	class Room {

		/**
		 *
		 * @param dimension dimensions of the room
		 * @param point the location of this room in the maze
		 * @param layer the Konva layer
		 */
		constructor(dimension, point, layer) {
			this.point = point;
			this.layer = layer;
			this.shape = null;

			const vm = this;
			const imageObj = new Image();
			imageObj.onload = new function () {
				vm.shape = new Konva.Image({
					x: dimension.width * point.x,
					y: dimension.height * point.y,
					height: dimension.height,
					width: dimension.width,
					image: imageObj,
				});
				layer.add(vm.shape);
				layer.draw();
			};
			imageObj.src = '/dungeon-tile.png';
		}

		toString() {
			return "Room(" + this.point + ")";
		}
	}

	/**
	 * A room that has a goal marker
	 */
	class GoalRoom extends Room {

		constructor(dimension, point, layer) {
			super(dimension, point, layer);
			this.marker = null;

			const vm = this;
			const imageObj = new Image();
			imageObj.onload = function () {
				vm.marker = new Konva.Image({
					image: imageObj,
					x: dimension.width * point.x,
					y: dimension.height * point.y,
					height: dimension.height,
					width: dimension.width
				});
				layer.add(vm.marker);
				layer.draw();
			};
			imageObj.src = '/alchemy-circle.png';
		}
	}

	class Runner {

		/**
		 *
		 * @param id the runner id
		 * @param name the runner name
		 * @param layer the Konva layer where this runner should be drawn
		 */
		constructor(id, name, layer) {
			this.id = id;
			this.name = name;
			this.point = new Point(-1, -1);
			this.avatar = null;
			this.animation = Promise.resolve();

			const vm = this;
			const image = new Image();
			image.src = '/ninja.png';
			image.onload = new function () {
				vm.avatar = new Konva.Sprite({
					image: image,
					animation: 'idle',
					animations: {
						idle: [
							// x, y, width, height
							6, 6, 52, 134
						],
						runLeft: [
							6, 149, 97, 96,
							110, 149, 111, 99,
							229, 149, 134, 94,
							371, 149, 136, 102,
							515, 149, 123, 99,
							646, 149, 83, 98,
							737, 149, 131, 94,
							876, 149, 140, 102,
							1024, 149, 140, 102
						],
						runRight: [
							6, 149, 97, 96,
							110, 149, 111, 99,
							229, 149, 134, 94,
							371, 149, 136, 102,
							515, 149, 123, 99,
							646, 149, 83, 98,
							737, 149, 131, 94,
							876, 149, 140, 102,
							1024, 149, 140, 102
						],
						runUp: [
							222, 280, 52, 108,
							281, 280, 57, 122,
							345, 280, 48, 133,
							400, 280, 41, 135,
							448, 280, 40, 122,
							495, 280, 47, 108,
							549, 280, 56, 124,
							612, 280, 52, 131,
							671, 280, 50, 126,
							728, 280, 50, 126
						],
						runDown: [
							270, 6, 50, 108,
							327, 6, 51, 97,
							385, 6, 57, 80,
							449, 6, 49, 77,
							504, 6, 41, 72,
							552, 6, 40, 109,
							599, 6, 47, 96,
							653, 6, 56, 82,
							716, 6, 52, 94,
							775, 6, 50, 108,
						],
						teleport: [
							1703, 602, 136, 258
						],
						dying: [
							1253, 602, 108, 124,
							1368, 602, 106, 107,
							1481, 602, 103, 86,
							1591, 602, 105, 83,
							382, 602, 147, 132,
							536, 602, 199, 165,
							742, 602, 137, 140,
							886, 602, 154, 160,
							1047, 602, 159, 171,
							1213, 602, 33, 29
						]
					},
					frameRate: 20,
					frameIndex: 0,
					height: 137,
					width: 53
				});
				layer.add(vm.avatar);
				vm.avatar.start();
			};
			this.layer = layer;
		}

		/**
		 * @param room the Room to go to
		 * @return a promise that resolves to the current runner
		 */
		moveTo(room) {
			if (this.avatar === null)
				return; // :(
			const vm = this;
			const stage = room.shape.getStage();
			const roomSize = room.shape.getSize();

			const velocity = 10;
			vm.animation = vm.animation.then(function () {
				return new Promise((resolve, reject) => {
					const anim = new Konva.Animation(function (frame) {
						const center = (function () {
							const cx = Math.round(roomSize.width - vm.avatar.width());
							const cy = Math.round(roomSize.height - vm.avatar.height());
							return {
								x: room.shape.x() + (cx / 2),
								y: room.shape.y() + (cy / 2)
							}
						})();

						const dx = center.x - vm.avatar.x();
						const dy = center.y - vm.avatar.y();
						let tooFast = false;
						let dist = velocity * (frame.time / 1000);
						if (dx > roomSize.width * 2 || dy > roomSize.height * 2) {
							dist *= 50; // faster
							tooFast = true;
						}

						let mx = dx < 0 ? Math.max(-dist, dx) : Math.min(dist, dx);
						let my = dy < 0 ? Math.max(-dist, dy) : Math.min(dist, dy);

						if (Math.round(dx) === 0 && Math.round(dy) === 0) {
							anim.stop();
							vm.avatar.animation('idle');
							resolve(vm); // we're done
						} else if (tooFast) {
							vm.avatar.animation('teleport');
						} else {
							vm.avatar.offsetX(vm.avatar.width() / 2);
							if (mx !== 0) {
								vm.avatar.scaleX(mx < 0 ? 1 : -1);
								vm.avatar.animation(mx < 0 ? 'runLeft' : 'runRight');
							}
							if (my !== 0)
								vm.avatar.animation(my < 0 ? 'runUp' : 'runDown');
						}

						vm.avatar.move({
							x: mx,
							y: my
						});
						return true;
					}, stage);
					anim.start();
				})
			});
			return vm.animation;
		}

		/**
		 * End of life for this runner
		 * @return a promise that resolves to this runner
		 */
		giveUp() {
			const vm = this;
			vm.animation = vm.animation.then(function () {
				return new Promise((resolve, reject) => {
					vm.avatar.animation('dying');
					setTimeout(function () {
						vm.layer.removeChildren(vm.avatar);
						vm.layer.draw();
						resolve(vm);
					}, 400); // FIXME compute timeout
				});
			});
			return vm.animation;
		}

		/**
		 * Winning stance for this runner
		 * @return a promise that resolves to this runner
		 */
		celebrate() {
		}
	}

	class Movement {

		/**
		 * @param runnerId the runner id
		 * @param runnerName the runner name
		 * @param type the type of movement
		 * @param point the new point
		 */
		constructor(runnerId, runnerName, type, point) {
			this.runnerId = runnerId;
			this.runnerName = runnerName;
			this.type = type;
			this.point = point;
		}
	}

	class Maze {

		/**
		 *
		 * @param columns the number of columns
		 * @param rows the number of rows
		 */
		constructor(columns, rows) {
			this.columns = columns;
			this.rows = rows;
			this.rooms = []; // Room[][]
			this.runners = {}; // hash(runner) => point
			this.runnersLayer = new Konva.Layer();
		}

		/**
		 *
		 * @param stage the Konva.Stage
		 * @param goal the goal Point
		 */
		init(stage, goal) {
			const roomHeight = (stage.height() / this.rows);
			const roomWidth = (stage.width() / this.columns);

			let layer = new Konva.Layer();
			let rooms = [];
			for (let row = 0; row < this.rows; row++) {
				let hall = [];
				for (let column = 0; column < this.columns; column++) {
					const pt = new Point(column, row);
					const dim = new Dimension(roomHeight, roomWidth);
					let room = null;
					if (goal.x === pt.x && goal.y === pt.y) {
						room = new GoalRoom(dim, pt, layer);
					} else {
						room = new Room(dim, pt, layer);
					}
					hall.push(room);
				}
				rooms.push(hall);
			}
			stage.add(layer);
			stage.add(this.runnersLayer);

			this.rooms = rooms
		}

		update(position) {
			const hash = btoa(position.runnerId + "[::]" + position.runnerName);
			this.runners[hash] = this.runners[hash]
				|| new Runner(position.runnerId, position.runnerName, this.runnersLayer);

			if (position.type === 'MOVED') {
				const room = this.rooms[position.point.y][position.point.x];
				this.runners[hash].moveTo(room);
			} else if (position.type === 'FAILED') {
				const runner = this.runners[hash];
				runner.giveUp();
				this.runners[hash] = null;
			} else if (position.type === 'FINISHED') {
				this.runners[hash].celebrate();
			} else {
				throw "Unexpected movement type: " + position.type;
			}
		}
	}

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
					vm.maze = new Maze(info.columns, info.rows);

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
