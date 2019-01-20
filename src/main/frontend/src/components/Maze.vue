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

	const windowPad = 50;
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
	}

	class Room {

		/**
		 *
		 * @param shape the Konva.Shape
		 * @param point the location of this room in the maze
		 */
		constructor(shape, point) {
			this.shape = shape;
			this.point = point;
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
			image.src = 'https://api.adorable.io/avatars/60/' + id;
			image.onload = new function () {
				vm.avatar = new Konva.Image({
					image: image,
					width: 60,
					height: 60
				});
				layer.add(vm.avatar);
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
			const velocity = 150;
			const time = 1000; // millis, how long to move to rooms

			vm.animation = vm.animation.then(function () {
				return new Promise((resolve, reject) => {
					const anim = new Konva.Animation(function (frame) {
						const roomSize = room.shape.getSize();
						const center = (function () {
							const cx = roomSize.width - vm.avatar.width();
							const cy = roomSize.height - vm.avatar.height();
							return {
								x: room.shape.x() + (cx / 2),
								y: room.shape.y() + (cy / 2)
							}
						})();

						const dx = center.x - vm.avatar.x();
						const dy = center.y - vm.avatar.y();
						let dist = velocity * (frame.timeDiff / time);
						if (dx > roomSize.width * 2 || dy > roomSize.height * 2)
							dist *= 50; // faster

						const mx = dx < 0 ? Math.max(-dist, dx) : Math.min(dist, dx);
						const my = dy < 0 ? Math.max(-dist, dy) : Math.min(dist, dy);

						if (Math.round(dx) === 0 && Math.round(dy) === 0) {
							anim.stop();
							resolve(vm); // we're done
						}
						vm.avatar.move({
							x: mx,
							y: my
						});
						vm.layer.draw();
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
					const anim = new Konva.Animation(function (frame) {
						vm.layer.removeChildren(vm.avatar);
						vm.layer.draw();
						anim.stop();
						resolve(vm);
					});
					anim.start();
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
		 */
		init(stage) {
			const roomPadding = 0.25;
			const roomHeight = (stage.height() / this.rows) - roomPadding;
			const roomWidth = (stage.width() / this.columns) - roomPadding;

			let layer = new Konva.Layer();
			let rooms = [];
			for (let row = 0; row < this.rows; row++) {
				let hall = [];
				for (let column = 0; column < this.columns; column++) {
					let rect = new Konva.Rect({
						x: roomWidth * column,
						y: roomHeight * row,
						height: roomHeight - windowPad / 4,
						width: roomWidth - windowPad / 4,
						fill: 'red',
						shadowBlur: 10
					});
					layer.add(rect);
					hall.push(new Room(rect, new Point(column, row)));
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
				const room = this.rooms[position.point.x][position.point.y];
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
			this.$http.get("/maze/info").then(response => {
				const info = response.body;
				vm.maze = new Maze(info.columns, info.rows);
				vm.maze.init(mazeRef.getStage());
				vm.$options.sockets.onmessage = function (event) {
					const position = vm.parsePosition(event.data);
					vm.maze.update(position);
				}
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
