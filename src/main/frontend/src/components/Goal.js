/**
 * The goal of the maze
 *
 * Draws a sprite to a layer
 */
import Dimension from "./Dimension";
import * as Konva from "konva";

export default class Goal {

	_imgUrl = '/goal.png';
	/**
	 * @type {Dimension}
	 */
	_dimension = new Dimension(62, 49);
	/**
	 * @type {Konva.Sprite}
	 */
	_shape;
	/**
	 * @type {Konva.Layer}
	 */
	_layer;

	/**
	 *
	 * @param {Konva.Layer} layer
	 */
	constructor(layer) {
		this._layer = layer;
		const vm = this;
		const img = new Image();
		const config = {
			animation: 'idle',
			animations: {
				idle: [
					// x, y, width, height
					44, 100, 47, 62,
					94, 100, 48, 62,
				],
			},
			frameRate: 2,
			frameIndex: 0,
			height: vm._dimension.height,
			width: vm._dimension.width,
			image: img
		};
		img.onload = new function () {
			vm._shape = new Konva.Sprite(config);
			vm._shape.start();
			vm._layer.add(vm._shape);
		};
		img.src = vm._imgUrl
	}

	/**
	 *
	 * @param {Point} point
	 */
	position(point) {
		this._shape.x(point.x);
		this._shape.y(point.y);
	}

	/**
	 *
	 * @return {Dimension}
	 */
	size() {
		return this._dimension;
	}
}
