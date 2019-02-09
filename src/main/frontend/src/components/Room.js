import Point from "./Point";
import * as Konva from "konva";

/**
 * A room based from Konva.
 *
 * This room draws a tile and positions to the provided Konva.Layer
 */
export default class Room {

	_imageUrls = [
		'/floors/obsidian-floor-plain.png',
		'/floors/obsidian-floor-vent.png',
		'/floors/obsidian-floor-vines.png'
	];
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
	 *
	 * @param {Dimension} dimension dimensions of the room
	 * @param {Point} origin the point at which this should be drawn
	 * @param {Konva.Layer} layer
	 */
	constructor(dimension, origin, layer) {
		this._size = dimension;
		this._origin = origin;

		const img = new Image();
		const config = {
			drawBorder: true,
			x: origin.x,
			y: origin.y,
			height: dimension.height,
			width: dimension.width,
			image: img
		};
		const vm = this;
		img.onload = new function () {
			vm._shape = new Konva.Image(config);
			layer.add(vm._shape);
		};
		img.src = vm._imageUrl()
	}

	/**
	 * @return {string} the image url to use
	 */
	_imageUrl() {
		const variations = this._imageUrls.length + 5;
		let num = Math.floor((Math.random() * variations) + 1);
		if (num > this._imageUrls.length - 1) {
			num = 0; // prioritize the "plain"
		}
		return this._imageUrls[num];
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
}
