import Point from "./Point";
import * as Konva from "konva";
import Dimension from "./Dimension";

/**
 * A room based from Konva.
 *
 * This room draws a tile and positions to the provided Konva.Layer
 */
export default class Room {

	_imgUrl = '/tile.png';
	/**
	 * @type {Point}
	 */
	_point;
	/**
	 * @type {Point}
	 */
	_origin;
	/**
	 * @type {Dimension}
	 */
	_size;
	/**
	 * @type {Konva.Image}
	 */
	_shape;
	/**
	 * @type {Konva.Layer}
	 */
	_layer;

	/**
	 *
	 * @param {Dimension} dimension dimensions of the room
	 * @param {Point} point the location of this room
	 * @param {Konva.Layer} layer
	 */
	constructor(dimension, point, layer) {
		this._size = dimension;
		this._point = point;
		this._origin = new Point(
			dimension.width * point.x,
			dimension.height * point.y
		);
		this._layer = layer;

		const img = new Image();
		const config = {
			drawBorder: true,
			x: this._origin.x,
			y: this._origin.y,
			height: dimension.height,
			width: dimension.width,
			image: img
		};
		const vm = this;
		img.onload = new function () {
			vm._shape = new Konva.Image(config);
			vm._layer.add(vm._shape);
		};
		img.src = vm._imgUrl
	}

	/**
	 * @return {Dimension}
	 */
	size() {
		return this._size;
	}

	/**
	 * Computes the top-left point at which the provided dimension
	 * can fit to be placed at center
	 *
	 * @param {Dimension} dimension
	 * @return {Point}
	 */
	center(dimension) {
		const x = this._shape.width() - dimension.width / 2;
		const y = this._shape.width() - dimension.height / 2;
		return new Point(this._origin.x + x, this._origin.y + y);
	}

	/**
	 *
	 * @param {Goal} goal
	 */
	put(goal) {
		const point = this.center(goal.size());
		goal.position(point);
	}

	toString() {
		return "Room(" + this._point + ")";
	}
}
