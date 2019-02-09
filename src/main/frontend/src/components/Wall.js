/**
 * Decorative wall
 */
import Konva from "konva";
import Point from "./Point";

export default class Wall {

	_imageUrls = [
		'/aesthetics/obsidian-wall.png',
		'/aesthetics/obsidian-wall-bottom.png'
	];

	/**
	 *
	 * @param {Dimension} dimension
	 * @param {Point} origin the point from where this should be drawn
	 * @param {Konva.Layer} layer
	 */
	constructor(dimension, origin, layer) {
		const w = dimension.width;
		const h = dimension.height / 2;
		const top = new Image();
		top.onload = new function () {
			const shape = new Konva.Image({
				x: origin.x,
				y: origin.y,
				height: h,
				width: w,
				image: top
			});
			layer.add(shape);
		};
		top.src = this._imageUrls[0];

		const bottom = new Image();
		bottom.onload = new function () {
			const shape = new Konva.Image({
				x: origin.x,
				y: (origin.y * 2) + h,
				height: h,
				width: w,
				image: bottom
			});
			layer.add(shape);
		};
		bottom.src = this._imageUrls[1];
	}
}
