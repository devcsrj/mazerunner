import * as Konva from "konva";
import Dimension from "./Dimension";
import Point from "./Point";
import Room from "./Room";
import Runner from "./Runner";
import Goal from "./Goal";

export default class Labyrinth {

	_columns;
	_rows;
	/**
	 * hash(runner) => point
	 *
	 * @type {{}}
	 */
	_runners = {};
	/**
	 * Room[][]
	 * @type {Array}
	 */
	_rooms = [];

	_layers = {
		rooms: new Konva.Layer(),
		runners: new Konva.Layer()
	};

	/**
	 *
	 * @param {number} columns the number of columns
	 * @param {number} rows the number of rows
	 */
	constructor(columns, rows) {
		if (columns <= 0) {
			throw Error("Column must be at least 1, got " + columns);
		}
		if (rows <= 0) {
			throw Error("Rows must be at least 1, got " + rows);
		}
		this._columns = columns;
		this._rows = rows;
	}

	/**
	 *
	 * @param {Konva.Stage} stage
	 * @param {Point} goal
	 */
	init(stage, goal) {
		const roomSize = new Dimension(
			stage.height() / this._rows,
			stage.width() / this._columns
		);

		for (let row = 0; row < this._rows; row++) {
			const hall = [];
			for (let column = 0; column < this._columns; column++) {
				const pt = new Point(column, row);
				let room = new Room(roomSize, pt, this._layers.rooms);
				hall.push(room);
			}
			this._rooms.push(hall);
		}

		this._rooms[goal.y][goal.x].put(new Goal(this._layers.rooms));

		stage.add(this._layers.rooms);
		stage.add(this._layers.runners);
		stage.batchDraw();

		const vm = this;
		setTimeout(function () {
			// For some reason, rooms aren't drawn right away :(
			vm._layers.rooms.draw();
		}, 1000);
	}

	/**
	 *
	 * @param {Movement} movement
	 */
	update(movement) {
		const hash = btoa(movement.runnerId + "[::]" + movement.runnerName);
		this._runners[hash] = this._runners[hash]
			|| new Runner(
				movement.runnerId,
				movement.runnerName,
				this._layers.runners);

		if (movement.type === 'MOVED') {
			const room = this._rooms[movement.point.y][movement.point.x];
			this._runners[hash].moveTo(room);
		} else if (movement.type === 'FAILED') {
			const runner = this._runners[hash];
			runner.giveUp();
			this._runners[hash] = null;
		} else if (movement.type === 'FINISHED') {
			this._runners[hash].celebrate();
		} else {
			throw "Unexpected movement type: " + movement.type;
		}
	}
}
